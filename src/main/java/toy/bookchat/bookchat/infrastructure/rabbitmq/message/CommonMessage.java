package toy.bookchat.bookchat.infrastructure.rabbitmq.message;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.chat.Chat;

@Getter
@EqualsAndHashCode(of = {"chatId", "senderId"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonMessage {

  private Long chatRoomId;
  private Long chatId;
  private Long senderId;
  private Integer receiptId;
  private String message;
  private String dispatchTime;

  @Builder
  private CommonMessage(Long chatRoomId, Long chatId, Long senderId, Integer receiptId, String dispatchTime,
      String message) {
    this.chatRoomId = chatRoomId;
    this.chatId = chatId;
    this.senderId = senderId;
    this.receiptId = receiptId;
    this.message = message;
    this.dispatchTime = dispatchTime;
  }

  public static CommonMessage from(Chat chat, Integer receiptId) {
    return CommonMessage.builder()
        .chatRoomId(chat.getChatRoomId())
        .chatId(chat.getId())
        .senderId(chat.getSenderId())
        .receiptId(receiptId)
        .message(chat.getMessage())
        .dispatchTime(chat.getDispatchTime().toString())
        .build();
  }
}
