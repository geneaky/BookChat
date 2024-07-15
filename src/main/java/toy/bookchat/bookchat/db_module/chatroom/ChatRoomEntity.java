package toy.bookchat.bookchat.db_module.chatroom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import toy.bookchat.bookchat.db_module.BaseEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;

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
    @Column(name = "book_id")
    private Long bookId;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity host;

    @Builder
    private ChatRoomEntity(Long id, String roomName, String roomSid, Integer roomSize, Integer defaultRoomImageType, String roomImageUri, Long bookId, UserEntity host) {
        this.id = id;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomSize = roomSize;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.bookId = bookId;
        this.host = host;
    }

    protected ChatRoomEntity() {
    }

    public void changeHost(UserEntity userEntity) {
        this.host = userEntity;
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
