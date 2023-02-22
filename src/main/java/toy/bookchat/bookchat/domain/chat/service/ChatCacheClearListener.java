package toy.bookchat.bookchat.domain.chat.service;

import static toy.bookchat.bookchat.domain.chat.service.ChatService.DESTINATION_PREFIX;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.CacheClearMessage;

@Component
public class ChatCacheClearListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatCacheService chatCacheService;
    private final ChatRepository chatRepository;


    public ChatCacheClearListener(SimpMessagingTemplate messagingTemplate,
        ChatCacheService chatCacheService, ChatRepository chatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatCacheService = chatCacheService;
        this.chatRepository = chatRepository;
    }

    @RabbitListener(queues = "cache.queue")
    public void clear(CacheClearMessage message) {
        /* TODO: 2023-02-22 at least once or timeout / cache 짧게 유지
         */
        chatCacheService.deleteParticipantCache(message.getUserId(), message.getChatRoomId());

        Chat chat = chatRepository.save(Chat.builder()
            .userIdForeignKey(message.getAdminId())
            .chatRoomIdForeignKey(message.getChatRoomId())
            .message(message.blockingComment())
            .build());

        messagingTemplate.convertAndSend(getDestination(message.getRoomSid()), chat.getMessage());
    }

    private String getDestination(String roomSid) {
        return DESTINATION_PREFIX + roomSid;
    }
}
