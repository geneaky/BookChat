package toy.bookchat.bookchat.domain.chat;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    protected Chat() {
    }

    @Builder
    private Chat(Long id, String message, User user, ChatRoom chatRoom) {
        this.id = id;
        this.message = message;
        this.user = user;
        this.chatRoom = chatRoom;
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

    public boolean isAnnouncementChat() {
        return this.user == null;
    }

    public Long getChatRoomId() {
        return this.chatRoom.getId();
    }
}
