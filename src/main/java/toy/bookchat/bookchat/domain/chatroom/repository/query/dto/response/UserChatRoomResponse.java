package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import toy.bookchat.bookchat.domain.book.Book;

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
    public UserChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount, Integer defaultRoomImageType, String roomImageUri, String bookTitle, String bookCoverImageUrl,
        List<String> bookAuthors, Long senderId, String senderNickname, String senderProfileImageUrl, Integer senderDefaultProfileImageType, Long lastChatId, String lastChatContent,
        LocalDateTime lastChatDispatchTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
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

    public UserChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount, Integer defaultRoomImageType, String roomImageUri, Long senderId, String senderNickname,
        String senderProfileImageUrl, Integer senderDefaultProfileImageType, Long lastChatId, String lastChatContent, LocalDateTime lastChatDispatchTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.senderDefaultProfileImageType = senderDefaultProfileImageType;
        this.lastChatId = lastChatId;
        this.lastChatContent = lastChatContent;
        this.lastChatDispatchTime = lastChatDispatchTime;
    }

    public void setBookInfo(Book book) {
        this.bookTitle = book.getTitle();
        this.bookCoverImageUrl = book.getBookCoverImageUrl();
        this.bookAuthors = book.getAuthors();
    }
}
