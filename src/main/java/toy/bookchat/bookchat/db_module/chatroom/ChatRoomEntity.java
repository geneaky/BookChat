package toy.bookchat.bookchat.db_module.chatroom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@DynamicInsert
@Table(name = "chat_room")
public class ChatRoomEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "book_id")
  private Long bookId;
  private String roomName;
  private String roomSid;
  private Integer roomSize;
  private Integer defaultRoomImageType;
  private String roomImageUri;

  @Builder
  private ChatRoomEntity(Long id, Long bookId, String roomName, String roomSid, Integer roomSize,
      Integer defaultRoomImageType,
      String roomImageUri) {
    this.id = id;
    this.bookId = bookId;
    this.roomName = roomName;
    this.roomSid = roomSid;
    this.roomSize = roomSize;
    this.defaultRoomImageType = defaultRoomImageType;
    this.roomImageUri = roomImageUri;
  }

  protected ChatRoomEntity() {
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
