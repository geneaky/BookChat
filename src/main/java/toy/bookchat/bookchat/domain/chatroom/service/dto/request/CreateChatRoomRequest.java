package toy.bookchat.bookchat.domain.chatroom.service.dto.request;

import java.time.LocalDate;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroomhost.ChatRoomHost;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChatRoomRequest {

    @NotBlank
    private String roomName;
    @Min(2)
    private Integer roomSize;
    @NotBlank
    private String isbn;
    @NotNull
    private LocalDate publishAt;

    @Builder
    private CreateChatRoomRequest(String roomName, Integer roomSize, String isbn,
        LocalDate publishAt) {
        this.roomName = roomName;
        this.roomSize = roomSize;
        this.isbn = isbn;
        this.publishAt = publishAt;
    }

    public ChatRoom makeChatRoom(Book book,
        ChatRoomHost chatRoomHost) {
        return ChatRoom.builder()
            .book(book)
            .roomSID(UUID.randomUUID().toString())
            .roomName(this.roomName)
            .roomSize(this.roomSize)
            .chatRoomHost(chatRoomHost)
            .build();
    }
}
