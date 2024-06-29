package toy.bookchat.bookchat.domain.chatroom;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.book.BookEntity;
import toy.bookchat.bookchat.domain.user.UserEntity;

@Getter
@Entity
@DynamicInsert
@Table(name = "chat_room")
public class ChatRoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
    private String roomSid;
    private Integer roomSize;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity bookEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity host;

    @Builder
    private ChatRoomEntity(Long id, String roomName, String roomSid, Integer roomSize, Integer defaultRoomImageType, String roomImageUri, BookEntity bookEntity, UserEntity host) {
        this.id = id;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomSize = roomSize;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.bookEntity = bookEntity;
        this.host = host;
    }

    protected ChatRoomEntity() {
    }

    public void changeHost(UserEntity userEntity) {
        this.host = userEntity;
    }

    public List<String> getBookAuthors() {
        return this.bookEntity.getAuthors();
    }

    public String getBookCoverImageUrl() {
        return this.bookEntity.getBookCoverImageUrl();
    }

    public String getBookTitle() {
        return this.bookEntity.getTitle();
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
}
