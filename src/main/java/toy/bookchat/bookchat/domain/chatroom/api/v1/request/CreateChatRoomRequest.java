package toy.bookchat.bookchat.domain.chatroom.api.v1.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.HashTag;
import toy.bookchat.bookchat.domain.chatroom.HashTags;

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

  public ChatRoom toChatRoom() {
    return ChatRoom.builder()
        .sid(UUID.randomUUID().toString())
        .name(this.roomName)
        .roomSize(this.roomSize)
        .defaultRoomImageType(this.defaultRoomImageType)
        .build();
  }

  public HashTags toHashTags() {
    List<HashTag> list = this.hashTags
        .stream()
        .map(tagName -> HashTag.builder().tagName(tagName).build())
        .collect(Collectors.toList());
    return new HashTags(list);
  }

  public Book toBook() {
    return this.bookRequest.extractBook();
  }
}
