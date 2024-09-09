package toy.bookchat.bookchat.domain.chatroom.api.v1.response;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomParticipantModel;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;

@Getter
@EqualsAndHashCode
public class ChatRoomDetails {

  private Integer roomSize;
  private List<String> roomTags;
  private String roomName;
  private String bookTitle;
  private String bookCoverImageUrl;
  private List<String> bookAuthors;
  private RoomHost roomHost;
  private List<RoomSubHost> roomSubHostList;
  private List<RoomGuest> roomGuestList;

  @Builder
  private ChatRoomDetails(Integer roomSize, List<String> roomTags, String roomName, String bookTitle,
      String bookCoverImageUrl, List<String> bookAuthors, RoomHost roomHost,
      List<RoomSubHost> roomSubHostList, List<RoomGuest> roomGuestList) {
    this.roomSize = roomSize;
    this.roomTags = roomTags;
    this.roomName = roomName;
    this.bookTitle = bookTitle;
    this.bookCoverImageUrl = bookCoverImageUrl;
    this.bookAuthors = bookAuthors;
    this.roomHost = roomHost;
    this.roomSubHostList = roomSubHostList;
    this.roomGuestList = roomGuestList;
  }

  public static ChatRoomDetails from(List<ChatRoomParticipantModel> chatRoomParticipantModels, List<String> roomTags,
      BookEntity bookEntity, ChatRoomEntity chatRoomEntity) {
    RoomHost roomHost = getHost(chatRoomParticipantModels);
    List<RoomSubHost> roomSubHostList = getSubHosts(chatRoomParticipantModels);
    List<RoomGuest> roomGuestList = getGuests(chatRoomParticipantModels);

    return new ChatRoomDetails(
        chatRoomEntity.getRoomSize(),
        roomTags,
        chatRoomEntity.getRoomName(),
        bookEntity.getTitle(),
        bookEntity.getBookCoverImageUrl(),
        bookEntity.getAuthors(),
        roomHost,
        roomSubHostList,
        roomGuestList
    );
  }

  private static List<RoomGuest> getGuests(List<ChatRoomParticipantModel> chatRoomParticipantModels) {
    return chatRoomParticipantModels.stream()
        .filter(crpm -> crpm.getStatus() == GUEST)
        .map(crpm -> RoomGuest.builder()
            .id(crpm.getUserId())
            .nickname(crpm.getNickname())
            .profileImageUrl(crpm.getProfileImageUrl())
            .defaultProfileImageType(crpm.getDefaultProfileImageType())
            .build())
        .collect(Collectors.toList());
  }

  private static List<RoomSubHost> getSubHosts(List<ChatRoomParticipantModel> chatRoomParticipantModels) {
    return chatRoomParticipantModels.stream()
        .filter(crpm -> crpm.getStatus() == SUBHOST)
        .map(crpm -> RoomSubHost.builder()
            .id(crpm.getUserId())
            .nickname(crpm.getNickname())
            .profileImageUrl(crpm.getProfileImageUrl())
            .defaultProfileImageType(crpm.getDefaultProfileImageType())
            .build())
        .collect(Collectors.toList());
  }

  private static RoomHost getHost(List<ChatRoomParticipantModel> chatRoomParticipantModels) {
    return chatRoomParticipantModels.stream()
        .filter(crpm -> crpm.getStatus() == ParticipantStatus.HOST)
        .findFirst()
        .map(crpm -> RoomHost.builder()
            .id(crpm.getUserId())
            .nickname(crpm.getNickname())
            .profileImageUrl(crpm.getProfileImageUrl())
            .defaultProfileImageType(crpm.getDefaultProfileImageType())
            .build())
        .orElseThrow(IllegalStateException::new);
  }
}
