package toy.bookchat.bookchat.infrastructure.fcm;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageBody {

  private Long chatId;
  private Long chatRoomId;

  @Builder
  private ChatMessageBody(Long chatId, Long chatRoomId) {
    this.chatId = chatId;
    this.chatRoomId = chatRoomId;
  }
}
