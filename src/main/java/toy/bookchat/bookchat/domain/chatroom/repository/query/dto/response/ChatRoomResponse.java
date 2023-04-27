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
    private String bookTitle;
    private String bookCoverImageUri;
    private List<String> bookAuthors;
    private String hostName;
    private Integer hostDefaultProfileImageType;
    private String hostProfileImageUri;
    private Long roomMemberCount;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    private String tags;
    private Long lastChatId;
    private LocalDateTime lastActiveTime;

    @Builder
    public ChatRoomResponse(Long roomId, String roomName, String roomSid,
        String bookTitle, String bookCoverImageUri, List<String> bookAuthors, String hostName,
        Integer hostDefaultProfileImageType, String hostProfileImageUri, Long roomMemberCount,
        Integer defaultRoomImageType,
        String roomImageUri, String tags, Long lastChatId, LocalDateTime lastActiveTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.bookTitle = bookTitle;
        this.bookCoverImageUri = bookCoverImageUri;
        this.bookAuthors = bookAuthors;
        this.hostName = hostName;
        this.hostDefaultProfileImageType = hostDefaultProfileImageType;
        this.hostProfileImageUri = hostProfileImageUri;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.tags = tags;
        this.lastChatId = lastChatId;
        this.lastActiveTime = lastActiveTime;
    }

    @Builder
    public ChatRoomResponse(Long roomId, String roomName, String roomSid,
        String bookTitle, String bookCoverImageUri, String hostName,
        Integer hostDefaultProfileImageType, String hostProfileImageUri, Long roomMemberCount,
        Integer defaultRoomImageType,
        String roomImageUri, String tags, Long lastChatId, LocalDateTime lastActiveTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.bookTitle = bookTitle;
        this.bookCoverImageUri = bookCoverImageUri;
        this.hostName = hostName;
        this.hostDefaultProfileImageType = hostDefaultProfileImageType;
        this.hostProfileImageUri = hostProfileImageUri;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.tags = tags;
        this.lastChatId = lastChatId;
        this.lastActiveTime = lastActiveTime;
    }

    public void setBookAuthors(List<String> bookAuthors) {
        this.bookAuthors = bookAuthors;
    }
}