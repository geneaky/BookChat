package toy.bookchat.bookchat.domain.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import toy.bookchat.bookchat.domain.chat.Chat;
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

    @Test
    void 채팅_내역_조회_성공() throws Exception {
        User aUser = User.builder()
            .id(1L)
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        User bUser = User.builder()
            .id(2L)
            .nickname("BUser")
            .profileImageUrl("bUser@s3.com")
            .defaultProfileImageType(1)
            .build();

        Chat chat1 = Chat.builder()
            .id(1L)
            .user(aUser)
            .message("first chat")
            .build();
        chat1.setCreatedAt(LocalDateTime.now());
        Chat chat2 = Chat.builder()
            .id(2L)
            .user(bUser)
            .message("second chat")
            .build();
        chat2.setCreatedAt(LocalDateTime.now());
        Chat chat3 = Chat.builder()
            .id(3L)
            .user(aUser)
            .message("welcome")
            .build();
        chat3.setCreatedAt(LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());
        SliceImpl<Chat> chatSlice = new SliceImpl<>(List.of(chat1, chat2, chat3), pageRequest,
            true);
        when(chatRepository.getChatRoomChats(any(), any(), any(), any())).thenReturn(chatSlice);
        chatService.getChatRoomChats(1L, Optional.empty(), mock(Pageable.class), 1L);

        verify(chatRepository).getChatRoomChats(any(), any(), any(), any());
    }
}