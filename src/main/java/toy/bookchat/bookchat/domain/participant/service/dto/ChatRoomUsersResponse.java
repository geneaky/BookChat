package toy.bookchat.bookchat.domain.participant.service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomUsersResponse {

    private RoomHost roomHost;
    private List<RoomSubHost> roomSubHostList;
    private List<RoomGuest> roomGuestList;

    @Builder
    private ChatRoomUsersResponse(RoomHost roomHost, List<RoomSubHost> roomSubHostList,
        List<RoomGuest> roomGuestList) {
        this.roomHost = roomHost;
        this.roomSubHostList = roomSubHostList;
        this.roomGuestList = roomGuestList;
    }
}
