package toy.bookchat.bookchat.db_module.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@Table(name = "chat")
public class ChatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    protected ChatEntity() {
    }

    @Builder
    private ChatEntity(Long id, String message, Long userId, Long chatRoomId) {
        this.id = id;
        this.message = message;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
    }

    public String getDispatchTime() {
        return getCreatedAt().toString();
    }
}
