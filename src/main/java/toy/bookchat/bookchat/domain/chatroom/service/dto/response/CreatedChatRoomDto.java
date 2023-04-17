package toy.bookchat.bookchat.domain.chatroom.service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatedChatRoomDto {

    private String roomSid;
    private Long roomId;

    @Builder
    private CreatedChatRoomDto(String roomSid, Long roomId) {
        this.roomSid = roomSid;
        this.roomId = roomId;
    }

    public String getRoomId() {
        return this.roomId.toString();
    }
}
