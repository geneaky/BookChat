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
import org.hibernate.annotations.DynamicInsert;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.user.User;

@Getter
@Entity
@DynamicInsert
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
    private Boolean isDeleted;

    @Builder
    private ChatRoom(Long id, String roomName, String roomSid, Integer roomSize, Integer defaultRoomImageType, String roomImageUri, Book book, User host, Boolean isDeleted) {
        this.id = id;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomSize = roomSize;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.book = book;
        this.host = host;
        this.isDeleted = isDeleted;
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

    public void changeRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void changeRoomSize(Integer roomSize) {
        this.roomSize = roomSize;
    }

    public void changeRoomImageUri(String roomImageUri) {
        this.roomImageUri = roomImageUri;
    }

    public void explode() {
        this.isDeleted = true;
    }
}
