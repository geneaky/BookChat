package toy.bookchat.bookchat.domain.chatroom;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoom {

  private final Long id;
  private final Long bookId;
  private final String sid;
  private final String name;
  private final String roomImageUri;
  private final Integer defaultRoomImageType;
  private final Integer roomSize;

  @Builder
  private ChatRoom(Long id, String sid, String name, Long bookId, Integer roomSize, String roomImageUri,
      Integer defaultRoomImageType) {
    this.id = id;
    this.sid = sid;
    this.name = name;
    this.bookId = bookId;
    this.roomSize = roomSize;
    this.roomImageUri = roomImageUri;
    this.defaultRoomImageType = defaultRoomImageType;
  }

  public ChatRoom withBookId(Long bookId) {
    return ChatRoom.builder()
        .id(this.id)
        .sid(this.sid)
        .name(this.name)
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
        .bookId(this.bookId)
        .roomSize(size)
        .roomImageUri(this.roomImageUri)
        .defaultRoomImageType(this.defaultRoomImageType)
        .build();
  }

  public ChatRoom withoutImageUrl() {
    return withImageUrl(null);
  }
}
