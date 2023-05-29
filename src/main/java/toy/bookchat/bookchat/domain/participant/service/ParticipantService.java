package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.participant.NoPermissionParticipantException;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.NotificationMessage;

@Service
public class ParticipantService {

    private final int SUB_HOST_COUNT = 5;

    private final ParticipantRepository participantRepository;
    private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    private final ChatRepository chatRepository;
    private final MessagePublisher messagePublisher;

    public ParticipantService(ParticipantRepository participantRepository,
        ChatRoomBlockedUserRepository chatRoomBlockedUserRepository, ChatRepository chatRepository,
        MessagePublisher messagePublisher) {
        this.participantRepository = participantRepository;
        this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
        this.chatRepository = chatRepository;
        this.messagePublisher = messagePublisher;
    }

    @Transactional
    public void changeParticipantRights(Long roomId, Long userId,
        ParticipantStatus participantStatus, Long requesterId) {
        Participant host = participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(
            requesterId, roomId).orElseThrow(NoPermissionParticipantException::new);

        validateIsHost(host.getUser(), host.getChatRoom());

        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        delegateSubHost(participantStatus, participant, roomId);
        dismissSubHost(participantStatus, participant);
        delegateHost(participantStatus, host, participant);
    }

    private void validateIsHost(User user, ChatRoom chatRoom) {
        if (chatRoom.getHost() != user) {
            throw new NoPermissionParticipantException();
        }
    }

    private void delegateHost(ParticipantStatus participantStatus, Participant host,
        Participant participant) {
        if (participantStatus == HOST) {
            host.toGuest();
            participant.toHost();
            participant.getChatRoom().changeHost(participant.getUser());
            Chat chat = chatRepository.save(Chat.builder()
                .chatRoom(participant.getChatRoom())
                .message(participant.getUserNickname() + "님이 방장이 되었습니다.")
                .build());
            messagePublisher.sendNotificationMessage(participant.getChatRoomSid(),
                NotificationMessage.createHostDelegateMessage(chat, participant.getUserId()));
        }
    }

    private void dismissSubHost(ParticipantStatus participantStatus, Participant participant) {
        if (participant.isSubHost() && participantStatus == GUEST) {
            participant.toGuest();
            Chat chat = chatRepository.save(Chat.builder()
                .chatRoom(participant.getChatRoom())
                .message(participant.getUserNickname() + "님이 부방장에서 해제되었습니다.")
                .build());
            messagePublisher.sendNotificationMessage(participant.getChatRoomSid(),
                NotificationMessage.createSubHostDismissMessage(chat, participant.getUserId()));
        }
    }

    private void delegateSubHost(ParticipantStatus participantStatus, Participant participant,
        Long roomId) {
        if (participantStatus == SUBHOST && participant.isNotSubHost()
            && participantRepository.countSubHostByRoomId(roomId) < SUB_HOST_COUNT) {
            participant.toSubHost();
            Chat chat = chatRepository.save(Chat.builder()
                .chatRoom(participant.getChatRoom())
                .message(participant.getUserNickname() + "님이 부방장이 되었습니다.")
                .build());
            messagePublisher.sendNotificationMessage(participant.getChatRoomSid(),
                NotificationMessage.createSubHostDelegateMessage(chat, participant.getUserId()));
        }
    }

    @Transactional
    public void kickParticipant(Long roomId, Long userId, Long adminId) {
        Participant adminParticipant = participantRepository.findByUserIdAndChatRoomId(adminId,
            roomId).orElseThrow(ParticipantNotFoundException::new);
        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        ChatRoomBlockedUser blockedUser = ChatRoomBlockedUser.builder()
            .user(participant.getUser())
            .chatRoom(participant.getChatRoom())
            .build();

        kick(adminParticipant, participant, blockedUser);

        Chat chat = chatRepository.save(Chat.builder()
            .chatRoom(adminParticipant.getChatRoom())
            .message(participant.getUserNickname() + "님을 내보냈습니다.")
            .build());
        messagePublisher.sendNotificationMessage(adminParticipant.getChatRoomSid(),
            NotificationMessage.createKickMessage(chat, participant.getUserId()));
    }

    private void kick(Participant adminParticipant, Participant targetParticipant,
        ChatRoomBlockedUser blockedUser) {
        if (adminParticipant.isSubHost() && targetParticipant.isGuest()) {
            deleteParticipant(targetParticipant, blockedUser);
            return;
        }
        if (adminParticipant.isHost() && targetParticipant.isNotHost()) {
            deleteParticipant(targetParticipant, blockedUser);
            return;
        }

        throw new NoPermissionParticipantException();
    }

    private void deleteParticipant(Participant targetParticipant, ChatRoomBlockedUser blockedUser) {
        participantRepository.delete(targetParticipant);
        chatRoomBlockedUserRepository.save(blockedUser);
    }
}
