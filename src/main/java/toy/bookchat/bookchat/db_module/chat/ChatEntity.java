package toy.bookchat.bookchat.db_module.chat;

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
import toy.bookchat.bookchat.db_module.BaseEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;

@Getter
@Entity
@Table(name = "chat")
public class ChatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoomEntity chatRoomEntity;

    protected ChatEntity() {
    }

    @Builder
    private ChatEntity(Long id, String message, UserEntity userEntity, ChatRoomEntity chatRoomEntity) {
        this.id = id;
        this.message = message;
        this.userEntity = userEntity;
        this.chatRoomEntity = chatRoomEntity;
    }

    public Long getUserId() {
        return this.userEntity.getId();
    }

    public String getUserNickname() {
        return this.userEntity.getNickname();
    }

    public String getUserProfileImageUrl() {
        return this.userEntity.getProfileImageUrl();
    }

    public Integer getUserDefaultProfileImageType() {
        return this.userEntity.getDefaultProfileImageType();
    }

    public String getDispatchTime() {
        return getCreatedAt().toString();
    }

    public boolean isAnnouncementChat() {
        return this.userEntity == null;
    }

    public Long getChatRoomId() {
        return this.chatRoomEntity.getId();
    }
}
