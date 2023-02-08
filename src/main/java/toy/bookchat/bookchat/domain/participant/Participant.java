package toy.bookchat.bookchat.domain.participant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Getter
public class Participant {

    @Id
    @GeneratedValue
    private Long id;
    private boolean isSubHost;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    protected Participant() {
    }

    @Builder
    private Participant(Long id, boolean isSubHost, User user, ChatRoom chatRoom) {
        this.id = id;
        this.isSubHost = isSubHost;
        this.user = user;
        this.chatRoom = chatRoom;
    }
}
