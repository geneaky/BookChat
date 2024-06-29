package toy.bookchat.bookchat.domain.chatroom;

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
import toy.bookchat.bookchat.domain.user.UserEntity;

@Getter
@Entity
@Table(name = "chat_room_blocked_user")
public class ChatRoomBlockedUserEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity userEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    ChatRoomEntity chatRoomEntity;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected ChatRoomBlockedUserEntity() {
    }

    @Builder
    private ChatRoomBlockedUserEntity(Long id, UserEntity userEntity, ChatRoomEntity chatRoomEntity) {
        this.id = id;
        this.userEntity = userEntity;
        this.chatRoomEntity = chatRoomEntity;
    }
}
