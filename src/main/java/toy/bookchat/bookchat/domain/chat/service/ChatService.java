package toy.bookchat.bookchat.domain.chat.service;

import static toy.bookchat.bookchat.infrastructure.fcm.PushType.CHAT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.ChatWithHost;
import toy.bookchat.bookchat.domain.chat.Message;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomReader;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.service.DeviceReader;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.service.ParticipantReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.infrastructure.fcm.ChatMessageBody;
import toy.bookchat.bookchat.infrastructure.fcm.PushMessageBody;
import toy.bookchat.bookchat.infrastructure.fcm.service.PushService;
import toy.bookchat.bookchat.infrastructure.rabbitmq.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.CommonMessage;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatReader chatReader;
  private final ChatAppender chatAppender;
  private final UserReader userReader;
  private final ChatRoomReader chatRoomReader;
  private final ParticipantValidator participantValidator;
  private final ParticipantReader participantReader;
  private final DeviceReader deviceReader;
  private final MessagePublisher messagePublisher;
  private final PushService pushService;

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
        .senderId(chat.getSenderId())
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
  public ChatWithHost getChatDetail(Long chatId, Long userId) {
    Chat chat = chatReader.readChat(userId, chatId);
    Host host = participantReader.readHost(chat.getChatRoomId());

    return ChatWithHost.builder()
        .chat(chat)
        .host(host)
        .build();
  }
}
