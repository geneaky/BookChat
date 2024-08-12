package toy.bookchat.bookchat.domain.chatroom.api.v1.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.UserChatRoomDetail;

@Getter
public class UserChatRoomDetailResponse {

  private Long roomId;
  private String roomName;
  private String roomSid;
  private Long roomMemberCount;
  private String roomImageUri;
  private Integer defaultRoomImageType;

  @Builder
  private UserChatRoomDetailResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount,
      String roomImageUri, Integer defaultRoomImageType) {
    this.roomId = roomId;
    this.roomName = roomName;
    this.roomSid = roomSid;
    this.roomImageUri = roomImageUri;
    this.defaultRoomImageType = defaultRoomImageType;
    this.roomMemberCount = roomMemberCount;
  }

  public static UserChatRoomDetailResponse from(UserChatRoomDetail userChatRoomDetail) {
    return UserChatRoomDetailResponse.builder()
        .roomId(userChatRoomDetail.getRoomId())
        .roomName(userChatRoomDetail.getRoomName())
        .roomSid(userChatRoomDetail.getRoomSid())
        .roomImageUri(userChatRoomDetail.getRoomImageUri())
        .defaultRoomImageType(userChatRoomDetail.getDefaultRoomImageType())
        .roomMemberCount(userChatRoomDetail.getRoomMemberCount())
        .build();
  }
}
