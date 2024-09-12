package toy.bookchat.bookchat.db_module.chatroom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@Table(name = "chat_room_hash_tag")
public class ChatRoomHashTagEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "chat_room_id", nullable = false)
  private Long chatRoomId;
  @Column(name = "hash_tag_id", nullable = false)
  private Long hashTagId;

  protected ChatRoomHashTagEntity() {
  }

  private ChatRoomHashTagEntity(Long chatRoomId, Long hashTagId) {
    this.chatRoomId = chatRoomId;
    this.hashTagId = hashTagId;
  }

  public static ChatRoomHashTagEntity of(Long chatRoomId, Long hashTagId) {
    return new ChatRoomHashTagEntity(chatRoomId, hashTagId);
  }
}
