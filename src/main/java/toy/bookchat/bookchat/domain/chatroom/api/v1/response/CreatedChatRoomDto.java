package toy.bookchat.bookchat.domain.chatroom.api.v1.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;

@Getter
public class CreatedChatRoomDto {

  private String roomSid;
  private String roomId;
  private String roomImageUri;

  @Builder
  private CreatedChatRoomDto(String roomSid, String roomId,
      String roomImageUri) {
    this.roomSid = roomSid;
    this.roomId = roomId;
    this.roomImageUri = roomImageUri;
  }

  public static CreatedChatRoomDto of(ChatRoomEntity chatRoomEntity) {
    return new CreatedChatRoomDto(chatRoomEntity.getRoomSid(), chatRoomEntity.getId().toString(),
        chatRoomEntity.getRoomImageUri());
  }
}
