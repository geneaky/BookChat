package toy.bookchat.bookchat.db_module.chatroom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "chat_room_blocked_user")
public class ChatRoomBlockedUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    protected ChatRoomBlockedUserEntity() {
    }

    @Builder
    private ChatRoomBlockedUserEntity(Long id, Long userId, Long chatRoomId) {
        this.id = id;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
    }
}
