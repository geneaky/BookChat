package toy.bookchat.bookchat.domain.chatroom;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserChatRoomDetail {

  private Long roomId;
  private String roomName;
  private String roomSid;
  private Long roomMemberCount;
  private String roomImageUri;
  private Integer defaultRoomImageType;

  @Builder
  private UserChatRoomDetail(Long roomId, String roomName, String roomSid, Long roomMemberCount, String roomImageUri,
      Integer defaultRoomImageType) {
    this.roomId = roomId;
    this.roomName = roomName;
    this.roomSid = roomSid;
    this.roomMemberCount = roomMemberCount;
    this.roomImageUri = roomImageUri;
    this.defaultRoomImageType = defaultRoomImageType;
  }

  public static UserChatRoomDetail from(ChatRoom chatRoom, Long roomMemberCount) {
    return UserChatRoomDetail.builder()
        .roomId(chatRoom.getId())
        .roomName(chatRoom.getName())
        .roomSid(chatRoom.getSid())
        .roomImageUri(chatRoom.getRoomImageUri())
        .defaultRoomImageType(chatRoom.getDefaultRoomImageType())
        .roomMemberCount(roomMemberCount)
        .build();
  }
}
