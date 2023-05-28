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
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.api.dto.request.MessageDto;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MessagePublisher messagingTemplate;
    @InjectMocks
    private ChatService chatService;

    @Test
    void 메시지_전송_성공() throws Exception {
        User user = User.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .build();

        Participant participant = Participant.builder()
            .id(1L)
            .chatRoom(chatRoom)
            .user(user)
            .build();

        Chat chat = Chat.builder()
            .id(1L)
            .message("test message")
            .build();
        chat.setCreatedAt(LocalDateTime.now());

        MessageDto messageDto = MessageDto.builder()
            .receiptId(1)
            .message(chat.getMessage())
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(participant));
        when(chatRepository.save(any())).thenReturn(chat);
        chatService.sendMessage(user.getId(), chatRoom.getId(), messageDto);

        verify(chatRepository).save(any());
        verify(messagingTemplate).sendCommonMessage(anyString(), any(CommonMessage.class));
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
        chatService.getChatRoomChats(1L, null, mock(Pageable.class), 1L);

        verify(chatRepository).getChatRoomChats(any(), any(), any(), any());
    }

    @Test
    void 채팅_내역_조회시_공지채팅과_일반채팅_구분하여_응답생성_성공() throws Exception {
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
        Chat chat4 = Chat.builder()
            .id(4L)
            .user(null)
            .message("announcement chat")
            .build();
        chat4.setCreatedAt(LocalDateTime.now());

        PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("id").descending());
        SliceImpl<Chat> chatSlice = new SliceImpl<>(List.of(chat1, chat2, chat3, chat4),
            pageRequest, true);
        when(chatRepository.getChatRoomChats(any(), any(), any(), any())).thenReturn(chatSlice);
        ChatRoomChatsResponse chatRoomChatsResponse = chatService.getChatRoomChats(1L, null,
            mock(Pageable.class), 1L);

        verify(chatRepository).getChatRoomChats(any(), any(), any(), any());
    }
}