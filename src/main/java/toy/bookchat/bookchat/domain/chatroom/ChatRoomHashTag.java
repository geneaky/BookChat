package toy.bookchat.bookchat.domain.chatroom;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;

@Entity
@Getter
public class ChatRoomHashTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom;

    @ManyToOne
    private HashTag hashTag;

    protected ChatRoomHashTag() {
    }

    private ChatRoomHashTag(ChatRoom chatRoom, HashTag hashTag) {
        this.chatRoom = chatRoom;
        this.hashTag = hashTag;
    }

    public static ChatRoomHashTag of(ChatRoom chatRoom, HashTag hashTag) {
        return new ChatRoomHashTag(chatRoom, hashTag);
    }
}
