package toy.bookchat.bookchat.domain.chatroom;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
    private String roomSid;
    private Integer roomSize;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    @ManyToOne(fetch = FetchType.LAZY)
    private User host;

    @Builder
    private ChatRoom(Long id, String roomName, String roomSid, Integer roomSize,
        Integer defaultRoomImageType, String roomImageUri, Book book, User host) {
        this.id = id;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomSize = roomSize;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.book = book;
        this.host = host;
    }

    protected ChatRoom() {
    }

    public void changeHost(User user) {
        this.host = user;
    }

    public List<String> getBookAuthors() {
        return this.book.getAuthors();
    }

    public String getBookCoverImageUrl() {
        return this.book.getBookCoverImageUrl();
    }

    public String getBookTitle() {
        return this.book.getTitle();
    }
}
