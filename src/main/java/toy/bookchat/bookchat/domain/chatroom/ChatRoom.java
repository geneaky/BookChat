package toy.bookchat.bookchat.domain.chatroom;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Getter
public class ChatRoom {

  private final Long id;
  private final Long hostId;
  private final Long bookId;
  private final String sid;
  private final String roomImageUri;
  private final Integer defaultRoomImageType;
  private final Integer roomSize;
  private final String name;

  @Builder
  private ChatRoom(Long id, String sid, String name, Long hostId, Long bookId, Integer roomSize, String roomImageUri,
      Integer defaultRoomImageType) {
    this.id = id;
    this.sid = sid;
    this.name = name;
    this.hostId = hostId;
    this.bookId = bookId;
    this.roomSize = roomSize;
    this.roomImageUri = roomImageUri;
    this.defaultRoomImageType = defaultRoomImageType;
  }

  public boolean isNotHost(User user) {
    return this.hostId != user.getId();
  }

  public boolean isHost(Long userId) {
    return this.hostId == userId;
  }

  public ChatRoom withHostId(Long hostId) {
    return ChatRoom.builder()
        .id(this.id)
        .sid(this.sid)
        .name(this.name)
        .hostId(hostId)
        .bookId(this.bookId)
        .roomSize(this.roomSize)
        .roomImageUri(this.roomImageUri)
        .defaultRoomImageType(this.defaultRoomImageType)
        .build();
  }

  public ChatRoom withBookId(Long bookId) {
    return ChatRoom.builder()
        .id(this.id)
        .sid(this.sid)
        .name(this.name)
        .hostId(this.hostId)
        .bookId(bookId)
        .roomSize(this.roomSize)
        .roomImageUri(this.roomImageUri)
        .defaultRoomImageType(this.defaultRoomImageType)
        .build();
  }

  public ChatRoom withImageUrl(String uploadFileUrl) {
    return ChatRoom.builder()
        .id(this.id)
        .sid(this.sid)
        .name(this.name)
        .hostId(this.hostId)
        .bookId(this.bookId)
        .roomSize(this.roomSize)
        .roomImageUri(uploadFileUrl)
        .defaultRoomImageType(this.defaultRoomImageType)
        .build();
  }

  public ChatRoom withName(String name) {
    return ChatRoom.builder()
        .id(this.id)
        .sid(this.sid)
        .name(name)
        .hostId(this.hostId)
        .bookId(this.bookId)
        .roomSize(this.roomSize)
        .roomImageUri(this.roomImageUri)
        .defaultRoomImageType(this.defaultRoomImageType)
        .build();
  }

  public ChatRoom withSize(Integer size) {
    return ChatRoom.builder()
        .id(this.id)
        .sid(this.sid)
        .name(this.name)
        .hostId(this.hostId)
        .bookId(this.bookId)
        .roomSize(size)
        .roomImageUri(this.roomImageUri)
        .defaultRoomImageType(this.defaultRoomImageType)
        .build();
  }
}
