package toy.bookchat.bookchat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.service.dto.request.ChatDto;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.chatroom.BlockedUserInChatRoomException;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomIsFullException;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatService chatService;

    @Test
    void 차단된_사용자가_입장시도시_예외발생() throws Exception {
        User user = mock(User.class);
        ChatRoom chatRoom = mock(ChatRoom.class);
        ChatRoomBlockedUser blockedUser = mock(ChatRoomBlockedUser.class);

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
        when(chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(blockedUser));

        assertThatThrownBy(() -> {
            chatService.enterChatRoom(69L, 988L);
        }).isInstanceOf(BlockedUserInChatRoomException.class);
    }

    @Test
    void 채팅방_인원수_가득_찼을_경우_예외발생() throws Exception {
        User user = User.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .roomSize(3)
            .build();

        Chat chat = Chat.builder()
            .user(user)
            .chatRoom(chatRoom)
            .message("test")
            .build();
        chat.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
        when(participantRepository.findWithPessimisticLockByChatRoom(any())).thenReturn(
            List.of(mock(Participant.class), mock(Participant.class), mock(Participant.class)));
        assertThatThrownBy(() -> {
            chatService.enterChatRoom(user.getId(), chatRoom.getId());
        }).isInstanceOf(ChatRoomIsFullException.class);
    }

    @Test
    void 채팅방_입장_성공() throws Exception {
        User user = User.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .roomSize(3)
            .build();

        Chat chat = Chat.builder()
            .user(user)
            .chatRoom(chatRoom)
            .message("test")
            .build();
        chat.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(chatRoomRepository.findById(any())).thenReturn(Optional.ofNullable(chatRoom));
        when(participantRepository.findWithPessimisticLockByChatRoom(any())).thenReturn(
            new ArrayList<>());
        when(chatRepository.save(any())).thenReturn(chat);
        chatService.enterChatRoom(user.getId(), chatRoom.getId());

        verify(participantRepository).save(any());
        verify(chatRepository).save(any());
        verify(messagingTemplate).convertAndSend(anyString(), any(ChatDto.class));
    }


    @Test
    void 방장이아닌_참가자_채팅방_퇴장_성공() throws Exception {
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
            .message("퇴장")
            .build();
        chat.setCreatedAt(LocalDateTime.now());

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(participant));
        when(chatRepository.save(any())).thenReturn(chat);
        chatService.leaveChatRoom(user.getId(), chatRoom.getId());

        verify(chatRepository).save(any());
        verify(participantRepository).delete(any());
        verify(messagingTemplate).convertAndSend(anyString(), any(ChatDto.class));
    }

    @Test
    void 방장의_채팅방_퇴장_성공() throws Exception {
        User user = User.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .host(user)
            .build();

        Participant participant = Participant.builder()
            .user(user)
            .chatRoom(chatRoom)
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(participant));
        chatService.leaveChatRoom(user.getId(), chatRoom.getId());

        verify(chatRepository).deleteByChatRoom(any());
        verify(participantRepository).deleteByChatRoom(any());
        verify(chatRoomRepository).delete(any());
    }

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

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(participant));
        when(chatRepository.save(any())).thenReturn(chat);
        chatService.sendMessage(user.getId(), chatRoom.getId(), "iM0Xf");

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