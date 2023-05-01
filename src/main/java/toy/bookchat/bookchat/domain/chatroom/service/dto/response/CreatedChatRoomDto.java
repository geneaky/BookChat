package toy.bookchat.bookchat.domain.chatroom.service.dto.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

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

    public static CreatedChatRoomDto of(ChatRoom chatRoom) {
        return new CreatedChatRoomDto(chatRoom.getRoomSid(), chatRoom.getId().toString(),
            chatRoom.getRoomImageUri());
    }
}
