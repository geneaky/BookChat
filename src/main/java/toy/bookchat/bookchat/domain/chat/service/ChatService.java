package toy.bookchat.bookchat.domain.chat.service;

import static toy.bookchat.bookchat.infrastructure.push.PushType.CHAT;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.Message;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomReader;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.service.DeviceReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.CommonMessage;
import toy.bookchat.bookchat.infrastructure.push.ChatMessageBody;
import toy.bookchat.bookchat.infrastructure.push.PushMessageBody;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;

@Service
public class ChatService {

  private final ChatReader chatReader;
  private final ChatAppender chatAppender;
  private final UserReader userReader;
  private final ChatRoomReader chatRoomReader;
  private final ParticipantValidator participantValidator;
  private final DeviceReader deviceReader;
  private final MessagePublisher messagePublisher;
  private final PushService pushService;

  public ChatService(UserReader userReader, ChatReader chatReader, ChatAppender chatAppender,
      ChatRoomReader chatRoomReader, ParticipantValidator participantValidator, DeviceReader deviceReader,
      MessagePublisher messagePublisher, PushService pushService) {
    this.userReader = userReader;
    this.chatReader = chatReader;
    this.chatAppender = chatAppender;
    this.chatRoomReader = chatRoomReader;
    this.participantValidator = participantValidator;
    this.deviceReader = deviceReader;
    this.messagePublisher = messagePublisher;
    this.pushService = pushService;
  }

  @Transactional
  public void sendMessage(Long userId, Long roomId, Message message) {
    participantValidator.checkDoesUserParticipate(userId, roomId);

    User user = userReader.readUser(userId);
    ChatRoom chatRoom = chatRoomReader.readChatRoom(roomId);
    Chat chat = chatAppender.append(user, chatRoom, message.getMessage());

    CommonMessage commonMessage = CommonMessage.from(chat, message.getReceiptId());

    ChatMessageBody chatMessageBody = ChatMessageBody.builder()
        .chatId(chat.getId())
        .chatRoomId(roomId)
        .build();
    messagePublisher.sendCommonMessage(chatRoom.getSid(), commonMessage);

    PushMessageBody pushMessageBody = PushMessageBody.of(CHAT, chatMessageBody);
    List<Device> disconnectedUserDevice = deviceReader.readDisconnectedUserDevice(roomId);
    for (Device device : disconnectedUserDevice) {
      pushService.send(device.getFcmToken(), pushMessageBody);
    }
  }

  @Transactional(readOnly = true)
  public Slice<Chat> getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable, Long userId) {
    return chatReader.readSlicedChat(userId, roomId, postCursorId, pageable);
  }

  @Transactional(readOnly = true)
  public Chat getChatDetail(Long chatId, Long userId) {
    return chatReader.readChat(userId, chatId);
  }
}
