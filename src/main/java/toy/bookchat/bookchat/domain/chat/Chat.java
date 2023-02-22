package toy.bookchat.bookchat.domain.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Getter
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    @Column(name = "user_id")
    private Long userIdForeignKey;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", insertable = false, updatable = false)
    private ChatRoom chatRoom;
    @Column(name = "chat_room_id")
    private Long chatRoomIdForeignKey;

    protected Chat() {
    }

    @Builder
    private Chat(Long id, String message, User user, Long userIdForeignKey, ChatRoom chatRoom,
        Long chatRoomIdForeignKey) {
        this.id = id;
        this.message = message;
        this.user = user;
        this.userIdForeignKey = userIdForeignKey;
        this.chatRoom = chatRoom;
        this.chatRoomIdForeignKey = chatRoomIdForeignKey;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public String getUserNickname() {
        return this.user.getNickname();
    }

    public String getUserProfileImageUrl() {
        return this.user.getProfileImageUrl();
    }

    public Integer getUserDefaultProfileImageType() {
        return this.user.getDefaultProfileImageType();
    }

    public String getDispatchTime() {
        return getCreatedAt().toString();
    }
}
