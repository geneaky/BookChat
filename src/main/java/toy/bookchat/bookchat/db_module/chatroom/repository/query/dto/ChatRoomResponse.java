package toy.bookchat.bookchat.db_module.chatroom.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ChatRoomResponse {

  private Long roomId;
  private String roomName;
  private String roomSid;
  private String bookTitle;
  private String bookCoverImageUri;
  private List<String> bookAuthors;
  private Long hostId;
  private String hostName;
  private Integer hostDefaultProfileImageType;
  private String hostProfileImageUri;
  private Long roomMemberCount;
  private Integer roomSize;
  private Integer defaultRoomImageType;
  private String roomImageUri;
  private String tags;
  private Long lastChatSenderId;
  private Long lastChatId;
  private String lastChatMessage;
  private LocalDateTime lastChatDispatchTime;

  @Builder
  public ChatRoomResponse(Long roomId, String roomName, String roomSid, String bookTitle, String bookCoverImageUri,
      List<String> bookAuthors, Long hostId, String hostName, Integer hostDefaultProfileImageType,
      String hostProfileImageUri, Long roomMemberCount, Integer roomSize, Integer defaultRoomImageType,
      String roomImageUri, String tags, Long lastChatSenderId, Long lastChatId, String lastChatMessage,
      LocalDateTime lastChatDispatchTime) {
    this.roomId = roomId;
    this.roomName = roomName;
    this.roomSid = roomSid;
    this.bookTitle = bookTitle;
    this.bookCoverImageUri = bookCoverImageUri;
    this.bookAuthors = bookAuthors;
    this.hostId = hostId;
    this.hostName = hostName;
    this.hostDefaultProfileImageType = hostDefaultProfileImageType;
    this.hostProfileImageUri = hostProfileImageUri;
    this.roomMemberCount = roomMemberCount;
    this.roomSize = roomSize;
    this.defaultRoomImageType = defaultRoomImageType;
    this.roomImageUri = roomImageUri;
    this.tags = tags;
    this.lastChatSenderId = lastChatSenderId;
    this.lastChatId = lastChatId;
    this.lastChatMessage = lastChatMessage;
    this.lastChatDispatchTime = lastChatDispatchTime;
  }

  @QueryProjection
  public ChatRoomResponse(Long roomId, String roomName, String roomSid, Integer defaultRoomImageType,
      String roomImageUri, Integer roomSize, Long roomMemberCount, String bookTitle, String bookCoverImageUri,
      Long hostId, String hostName, Integer hostDefaultProfileImageType, String hostProfileImageUri,
      String tags, Long lastChatSenderId, Long lastChatId, String lastChatMessage, LocalDateTime lastChatDispatchTime) {
    this.roomId = roomId;
    this.roomName = roomName;
    this.roomSid = roomSid;
    this.defaultRoomImageType = defaultRoomImageType;
    this.roomImageUri = roomImageUri;
    this.roomSize = roomSize;
    this.roomMemberCount = roomMemberCount;
    this.bookTitle = bookTitle;
    this.bookCoverImageUri = bookCoverImageUri;
    this.hostId = hostId;
    this.hostName = hostName;
    this.hostDefaultProfileImageType = hostDefaultProfileImageType;
    this.hostProfileImageUri = hostProfileImageUri;
    this.tags = tags;
    this.lastChatSenderId = lastChatSenderId;
    this.lastChatId = lastChatId;
    this.lastChatMessage = lastChatMessage;
    this.lastChatDispatchTime = lastChatDispatchTime;
  }

  public void setBookAuthors(List<String> bookAuthors) {
    this.bookAuthors = bookAuthors;
  }
}