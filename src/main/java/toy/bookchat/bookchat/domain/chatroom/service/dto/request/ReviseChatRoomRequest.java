package toy.bookchat.bookchat.domain.chatroom.service.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseChatRoomRequest {

    @NotNull
    private Long roomId;
    private String roomName;
    private Integer roomSize;

    @Builder
    private ReviseChatRoomRequest(Long roomId, String roomName, Integer roomSize) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSize = roomSize;
    }
}
