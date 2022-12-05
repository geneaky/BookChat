package toy.bookchat.bookchat.domain.chatroomhashtag;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.hashtag.HashTag;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"chat_room_id", "hash_tag_id"}
    )
})
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
