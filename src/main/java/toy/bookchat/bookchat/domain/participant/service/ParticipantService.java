package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.exception.forbidden.participant.NoPermissionParticipantException;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;
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
        ParticipantEntity host = participantRepository.findWithPessimisticLockByUserIdAndChatRoomId(
            requesterId, roomId).orElseThrow(NoPermissionParticipantException::new);

        validateIsHost(host.getUserEntity(), host.getChatRoomEntity());

        ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        delegateSubHost(participantStatus, participantEntity, roomId);
        dismissSubHost(participantStatus, participantEntity);
        delegateHost(participantStatus, host, participantEntity);
    }

    private void validateIsHost(UserEntity userEntity, ChatRoomEntity chatRoomEntity) {
        if (chatRoomEntity.getHost() != userEntity) {
            throw new NoPermissionParticipantException();
        }
    }

    private void delegateHost(ParticipantStatus participantStatus, ParticipantEntity host,
        ParticipantEntity participantEntity) {
        if (participantStatus == HOST) {
            host.toGuest();
            participantEntity.toHost();
            participantEntity.getChatRoomEntity().changeHost(participantEntity.getUserEntity());
            ChatEntity chatEntity = chatRepository.save(ChatEntity.builder()
                .chatRoomEntity(participantEntity.getChatRoomEntity())
                .message("#" + participantEntity.getUserId() + "#님이 방장이 되었습니다.")
                .build());
            messagePublisher.sendNotificationMessage(participantEntity.getChatRoomSid(),
                NotificationMessage.createHostDelegateMessage(chatEntity, participantEntity.getUserId()));
        }
    }

    private void dismissSubHost(ParticipantStatus participantStatus, ParticipantEntity participantEntity) {
        if (participantEntity.isSubHost() && participantStatus == GUEST) {
            participantEntity.toGuest();
            ChatEntity chatEntity = chatRepository.save(ChatEntity.builder()
                .chatRoomEntity(participantEntity.getChatRoomEntity())
                .message("#" + participantEntity.getUserId() + "#님이 부방장에서 해제되었습니다.")
                .build());
            messagePublisher.sendNotificationMessage(participantEntity.getChatRoomSid(),
                NotificationMessage.createSubHostDismissMessage(chatEntity, participantEntity.getUserId()));
        }
    }

    private void delegateSubHost(ParticipantStatus participantStatus, ParticipantEntity participantEntity,
        Long roomId) {
        if (participantStatus == SUBHOST && participantEntity.isNotSubHost()
            && participantRepository.countSubHostByRoomId(roomId) < SUB_HOST_COUNT) {
            participantEntity.toSubHost();
            ChatEntity chatEntity = chatRepository.save(ChatEntity.builder()
                .chatRoomEntity(participantEntity.getChatRoomEntity())
                .message("#" + participantEntity.getUserId() + "#님이 부방장이 되었습니다.")
                .build());
            messagePublisher.sendNotificationMessage(participantEntity.getChatRoomSid(),
                NotificationMessage.createSubHostDelegateMessage(chatEntity, participantEntity.getUserId()));
        }
    }

    @Transactional
    public void kickParticipant(Long roomId, Long userId, Long adminId) {
        ParticipantEntity adminParticipantEntity = participantRepository.findByUserIdAndChatRoomId(adminId,
            roomId).orElseThrow(ParticipantNotFoundException::new);
        ParticipantEntity participantEntity = participantRepository.findByUserIdAndChatRoomId(userId, roomId)
            .orElseThrow(ParticipantNotFoundException::new);

        ChatRoomBlockedUserEntity blockedUser = ChatRoomBlockedUserEntity.builder()
            .userEntity(participantEntity.getUserEntity())
            .chatRoomEntity(participantEntity.getChatRoomEntity())
            .build();

        kick(adminParticipantEntity, participantEntity, blockedUser);

        ChatEntity chatEntity = chatRepository.save(ChatEntity.builder()
            .chatRoomEntity(adminParticipantEntity.getChatRoomEntity())
            .message("#" + participantEntity.getUserId() + "#님을 내보냈습니다.")
            .build());
        messagePublisher.sendNotificationMessage(adminParticipantEntity.getChatRoomSid(), NotificationMessage.createKickMessage(chatEntity, participantEntity.getUserId()));
    }

    private void kick(ParticipantEntity adminParticipantEntity, ParticipantEntity targetParticipantEntity,
        ChatRoomBlockedUserEntity blockedUser) {
        if (adminParticipantEntity.isSubHost() && targetParticipantEntity.isGuest()) {
            deleteParticipant(targetParticipantEntity, blockedUser);
            return;
        }
        if (adminParticipantEntity.isHost() && targetParticipantEntity.isNotHost()) {
            deleteParticipant(targetParticipantEntity, blockedUser);
            return;
        }

        throw new NoPermissionParticipantException();
    }

    private void deleteParticipant(ParticipantEntity targetParticipantEntity, ChatRoomBlockedUserEntity blockedUser) {
        participantRepository.delete(targetParticipantEntity);
        chatRoomBlockedUserRepository.save(blockedUser);
    }
}
