package toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.book.Book;

@Getter
@EqualsAndHashCode
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
    private Long lastChatId;
    private String lastChatContent;

    @Builder
    public UserChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount,
        Integer defaultRoomImageType, String roomImageUri, String bookTitle,
        String bookCoverImageUrl, List<String> bookAuthors, Long lastChatId,
        String lastChatContent) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.bookTitle = bookTitle;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.bookAuthors = bookAuthors;
        this.lastChatId = lastChatId;
        this.lastChatContent = lastChatContent;
    }

    public UserChatRoomResponse(Long roomId, String roomName, String roomSid, Long roomMemberCount,
        Integer defaultRoomImageType, String roomImageUri, Long lastChatId,
        String lastChatContent) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomMemberCount = roomMemberCount;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.lastChatId = lastChatId;
        this.lastChatContent = lastChatContent;
    }

    public void setBookInfo(Book book) {
        this.bookTitle = book.getTitle();
        this.bookCoverImageUrl = book.getBookCoverImageUrl();
        this.bookAuthors = book.getAuthors();
    }
}
