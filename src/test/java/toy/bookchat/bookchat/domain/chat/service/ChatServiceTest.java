package toy.bookchat.bookchat.domain.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.Message;
import toy.bookchat.bookchat.domain.chat.Sender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomReader;
import toy.bookchat.bookchat.domain.device.service.DeviceReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.infrastructure.fcm.service.PushService;
import toy.bookchat.bookchat.infrastructure.rabbitmq.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.CommonMessage;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock
  private ChatReader chatReader;
  @Mock
  private ChatAppender chatAppender;
  @Mock
  private UserReader userReader;
  @Mock
  private ChatRoomReader chatRoomReader;
  @Mock
  private DeviceReader deviceReader;
  @Mock
  private ParticipantValidator participantValidator;
  @Mock
  private ParticipantReader participantReader;
  @Mock
  private PushService pushService;
  @Mock
  private MessagePublisher messagingTemplate;
  @InjectMocks
  private ChatService chatService;

  @Test
  void 메시지_전송_성공() throws Exception {
    ChatRoom chatRoom = ChatRoom.builder()
        .sid("testRoomSid")
        .build();
    given(chatRoomReader.readChatRoom(any())).willReturn(chatRoom);

    Sender sender = Sender.builder().id(1L).build();
    Chat chat = Chat.builder()
        .id(1L)
        .sender(sender)
        .chatRoomId(chatRoom.getId())
        .dispatchTime(LocalDateTime.now())
        .build();
    given(chatAppender.append(any(), any(), any())).willReturn(chat);

    Message message = Message.of(1, chat.getMessage());

    chatService.sendMessage(1L, 1L, message);

    verify(messagingTemplate).sendCommonMessage(anyString(), any(CommonMessage.class));
  }

  @Test
  void 채팅_내역_조회_성공() throws Exception {
    chatService.getChatRoomChats(1L, null, mock(Pageable.class), 1L);

    verify(chatReader).readSlicedChat(any(), any(), any(), any());
  }

  @Test
  void 채팅_내역_조회시_공지채팅과_일반채팅_구분하여_응답생성_성공() throws Exception {
    PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("id").descending());

    chatService.getChatRoomChats(1L, null, pageRequest, 1L);

    verify(chatReader).readSlicedChat(any(), any(), any(), any());
  }

  @Test
  void 채팅_채팅방_발신자정보를_조회_성공() throws Exception {
    Chat chat = Chat.builder()
        .chatRoomId(1L).build();
    given(chatReader.readChat(any(), any())).willReturn(chat);

    chatService.getChatDetail(1L, 1L);

    verify(participantReader).readHost(any());
  }
}