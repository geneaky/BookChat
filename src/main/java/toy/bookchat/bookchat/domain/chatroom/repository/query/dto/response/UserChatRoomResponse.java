package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import toy.bookchat.bookchat.domain.book.BookEntity;

@Getter
@EqualsAndHashCode
@ToString
public class UserChatRoomResponse {

    private Long roomId;
    private String roomName;
    private String roomSid;
    private Long roomMemberCount;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    private Long hostId;
    private String hostNickname;
    private String hostProfileImageUrl;
    private Integer hostDefaultProfileImageType;
    private String bookTitle;
    private String bookCoverImageUrl;
    private List<String> bookAuthors;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImageUrl;
    private Integer senderDefaultProfileImageType;
    private Long lastChatId;
    private String lastChatContent;
    private LocalDateTime lastChatDispatchTime;

    @Builder
    public UserChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount, Integer defaultRoomImageType, String roomImageUri, Long hostId, String hostNickname,
        String hostProfileImageUrl, Integer hostDefaultProfileImageType, String bookTitle, String bookCoverImageUrl,
        List<String> bookAuthors, Long senderId, String senderNickname, String senderProfileImageUrl, Integer senderDefaultProfileImageType, Long lastChatId, String lastChatContent,
        LocalDateTime lastChatDispatchTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.hostId = hostId;
        this.hostNickname = hostNickname;
        this.hostProfileImageUrl = hostProfileImageUrl;
        this.hostDefaultProfileImageType = hostDefaultProfileImageType;
        this.bookTitle = bookTitle;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.bookAuthors = bookAuthors;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.senderDefaultProfileImageType = senderDefaultProfileImageType;
        this.lastChatId = lastChatId;
        this.lastChatContent = lastChatContent;
        this.lastChatDispatchTime = lastChatDispatchTime;
    }

    public UserChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount, Integer defaultRoomImageType, String roomImageUri, Long hostId, String hostNickname,
        String hostProfileImageUrl, Integer hostDefaultProfileImageType, Long senderId, String senderNickname,
        String senderProfileImageUrl, Integer senderDefaultProfileImageType, Long lastChatId, String lastChatContent, LocalDateTime lastChatDispatchTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.hostId = hostId;
        this.hostNickname = hostNickname;
        this.hostProfileImageUrl = hostProfileImageUrl;
        this.hostDefaultProfileImageType = hostDefaultProfileImageType;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.senderDefaultProfileImageType = senderDefaultProfileImageType;
        this.lastChatId = lastChatId;
        this.lastChatContent = lastChatContent;
        this.lastChatDispatchTime = lastChatDispatchTime;
    }

    public void setBookInfo(BookEntity bookEntity) {
        this.bookTitle = bookEntity.getTitle();
        this.bookCoverImageUrl = bookEntity.getBookCoverImageUrl();
        this.bookAuthors = bookEntity.getAuthors();
    }
}
