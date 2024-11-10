package toy.bookchat.bookchat.infrastructure.fcm;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageBody {

  private Long chatId;
  private Long chatRoomId;
  private Long senderId;

  @Builder
  private ChatMessageBody(Long chatId, Long chatRoomId, Long senderId) {
    this.chatId = chatId;
    this.chatRoomId = chatRoomId;
    this.senderId = senderId;
  }
}
