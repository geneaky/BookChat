package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ChatRoomResponse {

    private Long roomId;
    private String roomName;
    private String roomSid;
    private Long roomMemberCount;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    private List<String> tags;
    private Long lastChatId;
    private LocalDateTime lastActiveTime;

    @Builder
    public ChatRoomResponse(Long roomId, String roomName, String roomSid,
        Long roomMemberCount, Integer defaultRoomImageType,
        String roomImageUri, List<String> tags, Long lastChatId, LocalDateTime lastActiveTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.tags = tags;
        this.lastChatId = lastChatId;
        this.lastActiveTime = lastActiveTime;
    }
}