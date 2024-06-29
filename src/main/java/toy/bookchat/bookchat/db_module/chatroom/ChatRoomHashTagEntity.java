package toy.bookchat.bookchat.db_module.chatroom;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@Table(name = "chat_room_hash_tag")
public class ChatRoomHashTagEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoomEntity chatRoomEntity;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hash_tag_id")
    private HashTagEntity hashTagEntity;

    protected ChatRoomHashTagEntity() {
    }

    private ChatRoomHashTagEntity(ChatRoomEntity chatRoomEntity, HashTagEntity hashTagEntity) {
        this.chatRoomEntity = chatRoomEntity;
        this.hashTagEntity = hashTagEntity;
    }

    public static ChatRoomHashTagEntity of(ChatRoomEntity chatRoomEntity, HashTagEntity hashTagEntity) {
        return new ChatRoomHashTagEntity(chatRoomEntity, hashTagEntity);
    }
}
