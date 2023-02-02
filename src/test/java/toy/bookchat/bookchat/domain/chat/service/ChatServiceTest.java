package toy.bookchat.bookchat.domain.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import toy.bookchat.bookchat.domain.chat.api.dto.ChatDto;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ChatCacheService chatCacheService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void 채팅방_입장_성공() throws Exception {
        User user = mock(User.class);

        when(user.getNickname()).thenReturn("testNick");
        when(chatCacheService.findUserByUserId(any())).thenReturn(user);
        chatService.enterChatRoom(1L, "testRoomSid");

        verify(participantRepository).save(any());
        verify(chatCacheService).saveParticipantCache(any(), any(), any());
        verify(chatRepository).save(any());
        verify(messagingTemplate).convertAndSend(anyString(), any(ChatDto.class));
    }

    @Test
    void 채팅방_퇴장_성공() throws Exception {
        User user = mock(User.class);

        when(user.getNickname()).thenReturn("testNick");
        when(chatCacheService.findUserByUserId(any())).thenReturn(user);
        chatService.leaveChatRoom(1L, "testRoomSid");

        verify(participantRepository).delete(any());
        verify(chatCacheService).deleteParticipantCache(any(), any());
        verify(chatRepository).save(any());
        verify(messagingTemplate).convertAndSend(anyString(), any(ChatDto.class));
    }

    @Test
    void 메시지_전송_성공() throws Exception {
        ChatDto chatDto = mock(ChatDto.class);
        chatService.sendMessage(1L, "testRoomSid", chatDto);

        verify(chatRepository).save(any());
        verify(messagingTemplate).convertAndSend(anyString(), any(ChatDto.class));
    }
}