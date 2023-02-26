package toy.bookchat.bookchat.domain.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.CacheClearMessage;

@ExtendWith(MockitoExtension.class)
class ChatCacheClearListenerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ChatCacheService chatCacheService;
    @Mock
    private ChatRepository chatRepository;
    @InjectMocks
    private ChatCacheClearListener chatCacheClearListener;

    @Test
    void 캐시_삭제_큐에_메시지_들어오면_사용자Id_채팅방Id로_참가자_캐시_삭제_성공() throws Exception {
        CacheClearMessage cacheClearMessage = CacheClearMessage.builder()
            .userId(538L)
            .chatRoomId(213L)
            .adminId(439L)
            .blockedUserNickname("owTfa")
            .roomSid("t3Q6AL")
            .build();

        Chat chat = Chat.builder()
            .message("5072J80i")
            .build();

        when(chatRepository.save(any())).thenReturn(chat);
        
        chatCacheClearListener.clear(cacheClearMessage);

        verify(chatCacheService).deleteParticipantCache(any(), any());
        verify(chatRepository).save(any());
        verify(messagingTemplate).convertAndSend(anyString(), anyString());
    }
}