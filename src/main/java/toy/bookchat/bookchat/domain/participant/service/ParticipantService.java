package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.config.rabbitmq.RabbitMQProperties.CACHE_CLEAR_EXCHANGE_NAME;
import static toy.bookchat.bookchat.config.rabbitmq.RabbitMQProperties.CACHE_CLEAR_ROUTING_KEY;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.CacheClearMessage;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomParticipantsResponse;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.exception.participant.NotHostException;
import toy.bookchat.bookchat.exception.participant.ParticipantNotFoundException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ParticipantService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    private final RabbitTemplate rabbitTemplate;

    public ParticipantService(UserRepository userRepository,
        ChatRoomRepository chatRoomRepository, ParticipantRepository participantRepository,
        ChatRoomBlockedUserRepository chatRoomBlockedUserRepository,
        RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
        this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    private static void validateIsHost(User user, ChatRoom chatRoom) {
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
    public void changeParticipantRights(Long roomId,
        Long userId, ParticipantStatus participantStatus,
        Long requesterId) {
        User requester = userRepository.findById(requesterId)
            .orElseThrow(UserNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(ChatRoomNotFoundException::new);

        validateIsHost(requester, chatRoom);

        Participant host = participantRepository.findByUserAndChatRoom(requester, chatRoom)
            .orElseThrow(ParticipantNotFoundException::new);
        Participant participant = participantRepository.findByUserAndChatRoom(user, chatRoom)
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
            chatRoom.changeHost(user);
        }
    }

    @Transactional
    public void deleteParticipant(Long roomId, Long userId, Long adminId) {
        User admin = userRepository.findById(adminId).orElseThrow(UserNotFoundException::new);
        User target = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(ChatRoomNotFoundException::new);
        Participant adminParticipant = participantRepository.findByUserAndChatRoom(admin, chatRoom)
            .orElseThrow(ParticipantNotFoundException::new);
        Participant targetParticipant = participantRepository.findByUserAndChatRoom(target,
                chatRoom)
            .orElseThrow(ParticipantNotFoundException::new);

        ChatRoomBlockedUser blockedUser = ChatRoomBlockedUser.builder()
            .user(target)
            .chatRoom(chatRoom).build();

        hostDeleteParticipant(chatRoom, adminParticipant, targetParticipant, blockedUser);
        subHostDeleteParticipant(chatRoom, adminParticipant, targetParticipant, blockedUser);
    }

    private void subHostDeleteParticipant(ChatRoom chatRoom, Participant adminParticipant,
        Participant targetParticipant, ChatRoomBlockedUser blockedUser) {
        if (adminParticipant.isSubHost() && targetParticipant.isGuest()) {
            participantRepository.delete(targetParticipant);
            chatRoomBlockedUserRepository.save(blockedUser);

            CacheClearMessage message = CacheClearMessage.builder()
                .blockedUserNickname(targetParticipant.getUserNickname())
                .adminId(adminParticipant.getId())
                .userId(targetParticipant.getUserId())
                .chatRoomId(chatRoom.getId())
                .roomSid(chatRoom.getRoomSid())
                .build();

            rabbitTemplate.convertAndSend(CACHE_CLEAR_EXCHANGE_NAME.getValue(),
                CACHE_CLEAR_ROUTING_KEY.getValue(), message);
        }
    }

    private void hostDeleteParticipant(ChatRoom chatRoom, Participant adminParticipant,
        Participant targetParticipant, ChatRoomBlockedUser blockedUser) {
        if (adminParticipant.isHost()) {
            participantRepository.delete(targetParticipant);
            chatRoomBlockedUserRepository.save(blockedUser);

            CacheClearMessage message = CacheClearMessage.builder()
                .blockedUserNickname(targetParticipant.getUserNickname())
                .adminId(adminParticipant.getId())
                .userId(targetParticipant.getUserId())
                .chatRoomId(chatRoom.getId())
                .roomSid(chatRoom.getRoomSid())
                .build();

            rabbitTemplate.convertAndSend(CACHE_CLEAR_EXCHANGE_NAME.getValue(),
                CACHE_CLEAR_ROUTING_KEY.getValue(), message);
        }
    }
}
