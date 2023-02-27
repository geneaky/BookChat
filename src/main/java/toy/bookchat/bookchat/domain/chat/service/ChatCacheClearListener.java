package toy.bookchat.bookchat.domain.chat.service;

import static toy.bookchat.bookchat.config.cache.CacheType.PARTICIPANT;
import static toy.bookchat.bookchat.domain.chat.service.ChatService.DESTINATION_PREFIX;

import javax.validation.Valid;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.CacheManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.CacheClearMessage;

@Component
public class ChatCacheClearListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final CacheManager cacheManager;


    public ChatCacheClearListener(SimpMessagingTemplate messagingTemplate,
        ChatRepository chatRepository, CacheManager cacheManager) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @RabbitListener(queues = "cache.queue")
    @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(delay = 1000))
    // participant cache 유효시간이 10초니까 실패하면 1초간격으로 10번 시도
    // 10번 모두 실패한 경우 강퇴된 사용자는 채팅 불가능
    public void clear(@Valid CacheClearMessage message) {
        String key = "U" + message.getUserId() + "CR" + message.getChatRoomId();
        if (cacheManager.getCache(PARTICIPANT.getCacheName()).evictIfPresent(key)) {
            Chat chat = chatRepository.save(Chat.builder()
                .userIdForeignKey(message.getAdminId())
                .chatRoomIdForeignKey(message.getChatRoomId())
                .message(message.blockingComment())
                .build());

            messagingTemplate.convertAndSend(getDestination(message.getRoomSid()),
                chat.getMessage());
        }
    }

    private String getDestination(String roomSid) {
        return DESTINATION_PREFIX + roomSid;
    }
}
