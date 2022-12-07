package toy.bookchat.bookchat.domain.chatroom;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;

@Entity
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
    private String roomSid;
    private Integer roomSize;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    @OneToOne(fetch = FetchType.LAZY)
    private ChatRoomHost chatRoomHost;
    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<Chat> chatList = new ArrayList<>();

    protected ChatRoom() {
    }

    @Builder
    private ChatRoom(Long id, String roomName, String roomSid, Integer roomSize,
        Integer defaultRoomImageType, String roomImageUri, Book book,
        ChatRoomHost chatRoomHost) {
        this.id = id;
        this.roomName = roomName;
        this.roomSid = roomSid;
        this.roomSize = roomSize;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.book = book;
        this.chatRoomHost = chatRoomHost;
    }
}
