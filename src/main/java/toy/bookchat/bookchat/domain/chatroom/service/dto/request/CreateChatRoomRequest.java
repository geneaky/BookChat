package toy.bookchat.bookchat.domain.chatroom.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.BookRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChatRoomRequest {

    @NotBlank
    private String roomName;
    @Min(2)
    private Integer roomSize;
    @NotNull
    private Integer defaultRoomImageType;
    private List<String> hashTags;
    @Valid
    @NotNull
    private BookRequest bookRequest;

    @Builder
    public CreateChatRoomRequest(String roomName, Integer roomSize, Integer defaultRoomImageType,
        List<String> hashTags,
        BookRequest bookRequest) {
        this.roomName = roomName;
        this.roomSize = roomSize;
        this.defaultRoomImageType = defaultRoomImageType;
        this.hashTags = hashTags;
        this.bookRequest = bookRequest;
    }

    public ChatRoomEntity makeChatRoom(Book book, UserEntity host, String fileUrl) {
        return ChatRoomEntity.builder()
            .bookId(book.getId())
            .roomSid(UUID.randomUUID().toString())
            .roomName(this.roomName)
            .roomSize(this.roomSize)
            .defaultRoomImageType(this.defaultRoomImageType)
            .roomImageUri(fileUrl)
            .hostId(host.getId())
            .build();
    }

    public Book createBook() {
        return this.bookRequest.extractBook();
    }

    @JsonIgnore
    public String getIsbn() {
        return this.bookRequest.getIsbn();
    }

    @JsonIgnore
    public LocalDate getPublishAt() {
        return this.bookRequest.getPublishAt();
    }
}
