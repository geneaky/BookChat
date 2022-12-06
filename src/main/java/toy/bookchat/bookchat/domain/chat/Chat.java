package toy.bookchat.bookchat.domain.chat;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    protected Chat() {
    }
}
