package toy.bookchat.bookchat.domain.chatroomhost;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Builder;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.user.User;

@Entity
public class ChatRoomHost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User mainHost;

    @OneToOne(fetch = FetchType.LAZY)
    private User subHost;

    @OneToOne(mappedBy = "chatRoomHost", fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    protected ChatRoomHost() {
    }

    @Builder
    private ChatRoomHost(Long id, User mainHost, User subHost,
        ChatRoom chatRoom) {
        this.id = id;
        this.mainHost = mainHost;
        this.subHost = subHost;
        this.chatRoom = chatRoom;
    }
}
