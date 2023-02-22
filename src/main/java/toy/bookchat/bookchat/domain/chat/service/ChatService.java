package toy.bookchat.bookchat.domain.chat.service;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.service.cache.ChatRoomCache;
import toy.bookchat.bookchat.domain.chat.service.cache.ParticipantCache;
import toy.bookchat.bookchat.domain.chat.service.cache.UserCache;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.exception.chatroom.BlockedUserInChatRoomException;
import toy.bookchat.bookchat.exception.participant.AlreadyParticipatedException;

@Service
public class ChatService {

    public static final String DESTINATION_PREFIX = "/topic/";
    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatCacheService chatCacheService;


    public ChatService(ChatRepository chatRepository, ParticipantRepository participantRepository,
        ChatRoomBlockedUserRepository chatRoomBlockedUserRepository,
        SimpMessagingTemplate messagingTemplate, ChatCacheService chatCacheService) {
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
        this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
        this.messagingTemplate = messagingTemplate;
        this.chatCacheService = chatCacheService;
    }

    private String getDestination(String roomSid) {
        return DESTINATION_PREFIX + roomSid;
    }

    @Transactional
    public void enterChatRoom(Long userId, String roomSid) {
        UserCache userCache = chatCacheService.findUserByUserId(userId);
        ChatRoomCache chatRoomCache = chatCacheService.findChatRoomByRoomSid(roomSid);

        /* TODO: 2023-02-08 채팅방 인원수 초과시 입장 불가 처리 - 동시성 제어 pessimistic lock */
        participantRepository.findByUserIdAndChatRoomId(userCache.getUserId(),
            chatRoomCache.getChatRoomId()).ifPresent(p -> {
            throw new AlreadyParticipatedException();
        });

        chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(userCache.getUserId(),
            chatRoomCache.getChatRoomId()).ifPresent(b -> {
            throw new BlockedUserInChatRoomException();
        });

        participantRepository.insertParticipantNativeQuery(userCache.getUserId(),
            chatRoomCache.getChatRoomId());

        Chat chat = chatRepository.save(Chat.builder()
            .userIdForeignKey(userCache.getUserId())
            .chatRoomIdForeignKey(chatRoomCache.getChatRoomId())
            .message(getWelcomeMessage(userCache))
            .build());

        messagingTemplate.convertAndSend(getDestination(roomSid), ChatDto.from(userCache, chat));
    }

    private String getWelcomeMessage(UserCache userCache) {
        return userCache.getUserNickname() + "님이 입장하셨습니다.";
    }

    @Transactional
    public void leaveChatRoom(Long userId, String roomSid) {
        UserCache userCache = chatCacheService.findUserByUserId(userId);
        ChatRoomCache chatRoomCache = chatCacheService.findChatRoomByRoomSid(roomSid);
        /* TODO: 2023-02-09 나가는 사람이 방장일 경우 처리 */
        ParticipantCache participantCache = chatCacheService.findParticipantByUserIdAndChatRoomId(
            userId, chatRoomCache.getChatRoomId());

        Chat chat = chatRepository.save(Chat.builder()
            .userIdForeignKey(userCache.getUserId())
            .chatRoomIdForeignKey(chatRoomCache.getChatRoomId())
            .message(getSendOffMessage(userCache))
            .build());

        participantRepository.deleteById(participantCache.getParticipantId());
        chatCacheService.deleteParticipantCache(userId, chatRoomCache.getChatRoomId());
        messagingTemplate.convertAndSend(getDestination(roomSid), ChatDto.from(userCache, chat));
    }

    private String getSendOffMessage(UserCache userCache) {
        return userCache.getUserNickname() + "님이 퇴장하셨습니다.";
    }

    @Transactional
    public void sendMessage(Long userId, String roomSid, ChatDto chatDto) {
        UserCache userCache = chatCacheService.findUserByUserId(userId);
        ChatRoomCache chatRoomCache = chatCacheService.findChatRoomByRoomSid(roomSid);
        chatCacheService.findParticipantByUserIdAndChatRoomId(userCache.getUserId(),
            chatRoomCache.getChatRoomId());

        Chat chat = chatRepository.save(Chat.builder()
            .userIdForeignKey(userCache.getUserId())
            .chatRoomIdForeignKey(chatRoomCache.getChatRoomId())
            .message(chatDto.getMessage())
            .build());

        messagingTemplate.convertAndSend(getDestination(roomSid), ChatDto.from(userCache, chat));
    }

    @Transactional(readOnly = true)
    public ChatRoomChatsResponse getChatRoomChats(Long roomId, Optional<Long> postCursorId,
        Pageable pageable, Long userId) {
        return new ChatRoomChatsResponse(
            chatRepository.getChatRoomChats(roomId, postCursorId, pageable, userId));
    }
}
