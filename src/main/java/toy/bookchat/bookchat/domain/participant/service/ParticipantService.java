package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomParticipantsResponse;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.participant.NotHostException;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;

    public ParticipantService(ParticipantRepository participantRepository,
        ChatRoomBlockedUserRepository chatRoomBlockedUserRepository) {
        this.participantRepository = participantRepository;
        this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
    }

    private void validateIsHost(User user, ChatRoom chatRoom) {
        if (chatRoom.getHost() != user) {
            throw new NotHostException();
        }
    }

    @Transactional(readOnly = true)
    public ChatRoomParticipantsResponse getChatRoomUsers(Long roomId, Long userId) {
        return ChatRoomParticipantsResponse.from(
            participantRepository.findChatRoomUsers(roomId, userId));
    }

    @Transactional
    public void changeParticipantRights(Long roomId, Long userId,
        ParticipantStatus participantStatus, Long requesterId) {
        Participant host = participantRepository.findByUserIdAndChatRoomId(requesterId, roomId)
            .orElseThrow(NotHostException::new);

        validateIsHost(host.getUser(), host.getChatRoom());

        Participant participant = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        if (participantStatus == SUBHOST) {
            participant.toSubHost();
        }

        if (participantStatus == GUEST) {
            participant.toGuest();
        }

        if (participantStatus == HOST) {
            host.toGuest();
            participant.toHost();
            participant.getChatRoom().changeHost(participant.getUser());
        }
    }

    @Transactional
    public void deleteParticipant(Long roomId, Long userId, Long adminId) {
        Participant adminParticipant = participantRepository.findByUserIdAndChatRoomId(adminId,
            roomId).orElseThrow(ParticipantNotFoundException::new);
        Participant targetParticipant = participantRepository.findByUserIdAndChatRoomId(userId,
                roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        ChatRoomBlockedUser blockedUser = ChatRoomBlockedUser.builder()
            .user(targetParticipant.getUser())
            .chatRoom(targetParticipant.getChatRoom())
            .build();

        hostDeleteParticipant(adminParticipant, targetParticipant, blockedUser);
    }

    private void hostDeleteParticipant(Participant adminParticipant, Participant targetParticipant,
        ChatRoomBlockedUser blockedUser) {
        if (adminParticipant.isSubHost() && targetParticipant.isGuest()) {
            kickParticipant(targetParticipant, blockedUser);
            return;
        }
        if (adminParticipant.isHost() && targetParticipant.isNotHost()) {
            kickParticipant(targetParticipant, blockedUser);
            return;
        }

        throw new NotHostException();
    }

    private void kickParticipant(Participant targetParticipant, ChatRoomBlockedUser blockedUser) {
        participantRepository.delete(targetParticipant);
        chatRoomBlockedUserRepository.save(blockedUser);
    }
}
