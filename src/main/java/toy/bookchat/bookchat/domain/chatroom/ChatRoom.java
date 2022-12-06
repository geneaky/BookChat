package toy.bookchat.bookchat.domain.chatroom;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Builder;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;

@Entity
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
    private String roomSID;
    private Integer roomSize;
    private Integer defaultRoomImageType;
    private String roomImageUri;
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    @OneToOne(fetch = FetchType.LAZY)
    private ChatRoomHost chatRoomHost;

    protected ChatRoom() {
    }

    @Builder
    private ChatRoom(Long id, String roomName, String roomSID, Integer roomSize,
        Integer defaultRoomImageType, String roomImageUri, Book book,
        ChatRoomHost chatRoomHost) {
        this.id = id;
        this.roomName = roomName;
        this.roomSID = roomSID;
        this.roomSize = roomSize;
        this.defaultRoomImageType = defaultRoomImageType;
        this.roomImageUri = roomImageUri;
        this.book = book;
        this.chatRoomHost = chatRoomHost;
    }
}
