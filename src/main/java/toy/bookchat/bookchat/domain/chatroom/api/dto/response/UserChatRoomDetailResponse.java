package toy.bookchat.bookchat.domain.chatroom.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;

@Getter
public class UserChatRoomDetailResponse {

    private Long roomId;
    private String roomName;
    private String roomSid;
    private Long roomMemberCount;
    private String roomImageUri;
    private Integer defaultRoomImageType;

    @Builder
    private UserChatRoomDetailResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount, String roomImageUri, Integer defaultRoomImageType) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomImageUri = roomImageUri;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomMemberCount = roomMemberCount;
    }

    public static UserChatRoomDetailResponse from(ChatRoomEntity chatroom, Long roomMemberCount) {
        return UserChatRoomDetailResponse.builder()
            .roomId(chatroom.getId())
            .roomName(chatroom.getRoomName())
            .roomSid(chatroom.getRoomSid())
            .roomImageUri(chatroom.getRoomImageUri())
            .defaultRoomImageType(chatroom.getDefaultRoomImageType())
            .roomMemberCount(roomMemberCount)
            .build();
    }
}
