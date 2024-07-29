package toy.bookchat.bookchat.domain.chatroom;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Getter
public class ChatRoom {

  private final Long id;
  private final String sid;
  private final Long hostId;
  private final Integer roomSize;

  @Builder
  private ChatRoom(Long id, String sid, Long hostId, Integer roomSize) {
    this.id = id;
    this.sid = sid;
    this.hostId = hostId;
    this.roomSize = roomSize;
  }

  public boolean isNotHost(User user) {
    return this.hostId != user.getId();
  }

  public boolean isHost(Long userId) {
    return this.hostId == userId;
  }
}
