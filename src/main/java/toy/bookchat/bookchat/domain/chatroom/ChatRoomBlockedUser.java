package toy.bookchat.bookchat.domain.chatroom;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Getter
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "chat_room_id"})
    }
)
public class ChatRoomBlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;
    @ManyToOne(fetch = FetchType.LAZY)
    ChatRoom chatRoom;

    protected ChatRoomBlockedUser() {
    }

    @Builder
    private ChatRoomBlockedUser(Long id, User user, ChatRoom chatRoom) {
        this.id = id;
        this.user = user;
        this.chatRoom = chatRoom;
    }
}
