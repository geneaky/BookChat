package toy.bookchat.bookchat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chat.api.dto.request.MessageDto;
import toy.bookchat.bookchat.domain.chat.api.dto.response.ChatDetailResponse;
import toy.bookchat.bookchat.domain.chat.api.dto.response.ChatSender;
import toy.bookchat.bookchat.domain.chat.service.dto.response.ChatRoomChatsResponse;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.device.repository.DeviceRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.exception.badrequest.participant.NotParticipatedException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private PushService pushService;
    @Mock
    private MessagePublisher messagingTemplate;
    @InjectMocks
    private ChatService chatService;

    @Test
    void 메시지_전송_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("testNick")
            .profileImageUrl("testImage")
            .defaultProfileImageType(1)
            .build();

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .id(1L)
            .roomSid("testRoomSid")
            .build();

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .id(1L)
            .chatRoomEntity(chatRoomEntity)
            .userEntity(userEntity)
            .build();

        ChatEntity chatEntity = ChatEntity.builder()
            .id(1L)
            .chatRoomEntity(chatRoomEntity)
            .message("test message")
            .build();
        chatEntity.setCreatedAt(LocalDateTime.now());

        MessageDto messageDto = MessageDto.builder()
            .receiptId(1)
            .message(chatEntity.getMessage())
            .build();

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.ofNullable(participantEntity));
        when(chatRepository.save(any())).thenReturn(chatEntity);
        chatService.sendMessage(userEntity.getId(), chatRoomEntity.getId(), messageDto);

        verify(chatRepository).save(any());
        verify(messagingTemplate).sendCommonMessage(anyString(), any(CommonMessage.class));
    }

    @Test
    void 채팅_내역_조회_성공() throws Exception {
        UserEntity aUserEntity = UserEntity.builder()
            .id(1L)
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        UserEntity bUserEntity = UserEntity.builder()
            .id(2L)
            .nickname("BUser")
            .profileImageUrl("bUser@s3.com")
            .defaultProfileImageType(1)
            .build();

        ChatEntity chatEntity1 = ChatEntity.builder()
            .id(1L)
            .userEntity(aUserEntity)
            .message("first chat")
            .build();
        chatEntity1.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity2 = ChatEntity.builder()
            .id(2L)
            .userEntity(bUserEntity)
            .message("second chat")
            .build();
        chatEntity2.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity3 = ChatEntity.builder()
            .id(3L)
            .userEntity(aUserEntity)
            .message("welcome")
            .build();
        chatEntity3.setCreatedAt(LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());
        SliceImpl<ChatEntity> chatSlice = new SliceImpl<>(List.of(chatEntity1, chatEntity2, chatEntity3), pageRequest,
            true);
        when(chatRepository.getChatRoomChats(any(), any(), any(), any())).thenReturn(chatSlice);
        chatService.getChatRoomChats(1L, null, mock(Pageable.class), 1L);

        verify(chatRepository).getChatRoomChats(any(), any(), any(), any());
    }

    @Test
    void 채팅_내역_조회시_공지채팅과_일반채팅_구분하여_응답생성_성공() throws Exception {
        UserEntity aUserEntity = UserEntity.builder()
            .id(1L)
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        UserEntity bUserEntity = UserEntity.builder()
            .id(2L)
            .nickname("BUser")
            .profileImageUrl("bUser@s3.com")
            .defaultProfileImageType(1)
            .build();

        ChatEntity chatEntity1 = ChatEntity.builder()
            .id(1L)
            .userEntity(aUserEntity)
            .message("first chat")
            .build();
        chatEntity1.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity2 = ChatEntity.builder()
            .id(2L)
            .userEntity(bUserEntity)
            .message("second chat")
            .build();
        chatEntity2.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity3 = ChatEntity.builder()
            .id(3L)
            .userEntity(aUserEntity)
            .message("welcome")
            .build();
        chatEntity3.setCreatedAt(LocalDateTime.now());
        ChatEntity chatEntity4 = ChatEntity.builder()
            .id(4L)
            .userEntity(null)
            .message("announcement chat")
            .build();
        chatEntity4.setCreatedAt(LocalDateTime.now());

        PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("id").descending());
        SliceImpl<ChatEntity> chatSlice = new SliceImpl<>(List.of(chatEntity1, chatEntity2, chatEntity3, chatEntity4),
            pageRequest, true);
        when(chatRepository.getChatRoomChats(any(), any(), any(), any())).thenReturn(chatSlice);
        ChatRoomChatsResponse chatRoomChatsResponse = chatService.getChatRoomChats(1L, null,
            mock(Pageable.class), 1L);

        verify(chatRepository).getChatRoomChats(any(), any(), any(), any());
    }

    @Test
    void 사용자가_참여하지않은_채팅방_채팅_조회_실패() throws Exception {
        assertThatThrownBy(() -> chatService.getChatDetail(1L, 1L))
            .isInstanceOf(NotParticipatedException.class);
    }

    @Test
    void 채팅_채팅방_발신자정보를_조회_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .nickname("EWNrSKNRR")
            .profileImageUrl("Dv1TTe0uJn")
            .defaultProfileImageType(1)
            .build();
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder().id(1L).build();
        ChatEntity chatEntity = ChatEntity.builder()
            .userEntity(userEntity)
            .chatRoomEntity(chatRoomEntity)
            .message("TTuaihiP")
            .build();
        given(chatRepository.getUserChatRoomChat(any(), any())).willReturn(Optional.of(chatEntity));

        ChatDetailResponse chatDetail = chatService.getChatDetail(1L, 1L);

        assertThat(chatDetail).extracting(ChatDetailResponse::getChatId, ChatDetailResponse::getChatRoomId, ChatDetailResponse::getMessage, ChatDetailResponse::getSender)
            .containsExactly(chatEntity.getId(), chatRoomEntity.getId(), chatEntity.getMessage(), ChatSender.from(userEntity));
    }
}