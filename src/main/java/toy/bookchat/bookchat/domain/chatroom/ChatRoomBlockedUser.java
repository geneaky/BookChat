package toy.bookchat.bookchat.domain.chatroom;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomBlockedUser {

  private final Long userId;
  private final Long chatRoomId;

  @Builder
  private ChatRoomBlockedUser(Long userId, Long chatRoomId) {
    this.userId = userId;
    this.chatRoomId = chatRoomId;
  }
}
