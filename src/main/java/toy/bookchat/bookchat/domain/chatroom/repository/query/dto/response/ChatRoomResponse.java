package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChatRoomResponse {

    private Long roomId;
    private String roomName;
    private String roomSid;
    private Long roomMemberCount;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    private LocalDateTime lastActiveTime;
    private String lastChatContent;

    @Builder
    public ChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount,
        Integer defaultRoomImageType,
        String roomImageUri, LocalDateTime lastActiveTime, String lastChatContent) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.lastActiveTime = lastActiveTime;
        this.lastChatContent = lastChatContent;
    }
}
