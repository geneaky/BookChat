package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserChatRoomResponse {

    private Long roomId;
    private String roomName;
    private String roomSid;
    private Long roomMemberCount;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    private Long lastChatId;
    private LocalDateTime lastActiveTime;
    private String lastChatContent;

    @Builder
    public UserChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount,
        Integer defaultRoomImageType, String roomImageUri, Long lastChatId,
        LocalDateTime lastActiveTime,
        String lastChatContent) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.lastChatId = lastChatId;
        this.lastActiveTime = lastActiveTime;
        this.lastChatContent = lastChatContent;
    }
}
