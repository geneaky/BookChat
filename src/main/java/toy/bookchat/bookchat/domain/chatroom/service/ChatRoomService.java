package toy.bookchat.bookchat.domain.chatroom.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.service.ChatAppender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.HashTag;
import toy.bookchat.bookchat.domain.chatroom.HashTags;
import toy.bookchat.bookchat.domain.chatroom.UserChatRoomDetail;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.service.ParticipantAppender;
import toy.bookchat.bookchat.domain.participant.service.ParticipantCleaner;
import toy.bookchat.bookchat.domain.participant.service.ParticipantReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.infrastructure.s3.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.infrastructure.rabbitmq.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.rabbitmq.message.NotificationMessage;

@Service
public class ChatRoomService {

  private final StorageService storageService;
  private final BookReader bookReader;
  private final UserReader userReader;
  private final ChatRoomReader chatRoomReader;
  private final ChatRoomUserValidator chatRoomUserValidator;
  private final MessagePublisher messagePublisher;
  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private final ParticipantAppender participantAppender;
  private final ParticipantValidator participantValidator;
  private final ParticipantCleaner participantCleaner;
  private final ChatAppender chatAppender;
  private final ParticipantReader participantReader;
  private final ChatRoomAppender chatRoomAppender;
  private final HashTagAppender hashTagAppender;
  private final ChatRoomHashTagAppender chatRoomHashTagAppender;
  private final ChatRoomHashTagCleaner chatRoomHashTagCleaner;
  private final ChatRoomManager chatRoomManager;

  public ChatRoomService(@Qualifier("chatRoomStorageService") StorageService storageService, BookReader bookReader,
      UserReader userReader, ChatRoomReader chatRoomReader, ChatRoomUserValidator chatRoomUserValidator,
      MessagePublisher messagePublisher, ParticipantAppender participantAppender,
      ParticipantValidator participantValidator, ParticipantCleaner participantCleaner, ChatAppender chatAppender,
      ParticipantReader participantReader, ChatRoomAppender chatRoomAppender, HashTagAppender hashTagAppender,
      ChatRoomHashTagAppender chatRoomHashTagAppender, ChatRoomHashTagCleaner chatRoomHashTagCleaner,
      ChatRoomManager chatRoomManager) {
    this.storageService = storageService;
    this.bookReader = bookReader;
    this.userReader = userReader;
    this.chatRoomReader = chatRoomReader;
    this.chatRoomUserValidator = chatRoomUserValidator;
    this.messagePublisher = messagePublisher;
    this.participantAppender = participantAppender;
    this.participantValidator = participantValidator;
    this.participantCleaner = participantCleaner;
    this.chatAppender = chatAppender;
    this.participantReader = participantReader;
    this.chatRoomAppender = chatRoomAppender;
    this.hashTagAppender = hashTagAppender;
    this.chatRoomHashTagAppender = chatRoomHashTagAppender;
    this.chatRoomHashTagCleaner = chatRoomHashTagCleaner;
    this.chatRoomManager = chatRoomManager;
  }

  @Transactional
  public Long createChatRoom(ChatRoom chatRoom, HashTags hashTags, Book chatRoomBook, MultipartFile chatRoomImage,
      Long userId) {
    Book book = bookReader.readBook(chatRoomBook);
    User host = userReader.readUser(userId);

    chatRoom = chatRoom.withBookId(book.getId());

    if (hasImage(chatRoomImage)) {
      String uploadFileUrl = storageService.upload(chatRoomImage, UUID.randomUUID().toString(),
          LocalDateTime.now().format(dateTimeFormatter));
      chatRoom = chatRoom.withImageUrl(uploadFileUrl);
    }

    Long chatRoomId = chatRoomAppender.append(chatRoom);
    participantAppender.append(host.getId(), chatRoomId, HOST);

    for (HashTag hashTag : hashTags.getList()) {
      HashTag storedHashTag = hashTagAppender.append(hashTag);
      chatRoomHashTagAppender.append(chatRoomId, storedHashTag);
    }

    return chatRoomId;
  }

  @Transactional(readOnly = true)
  public Slice<UserChatRoomResponse> getUserChatRooms(Long bookId, Long postCursorId, Pageable pageable, Long userId) {
    return chatRoomReader.readSliceUserChatRooms(userId, bookId, postCursorId, pageable);
  }

  @Transactional(readOnly = true)
  public UserChatRoomDetail getUserChatRoomDetails(Long roomId, Long userId) {
    ChatRoom chatRoom = chatRoomReader.readChatRoom(userId, roomId);
    Long roomMemberCount = participantReader.readParticipantCount(chatRoom);

    return UserChatRoomDetail.from(chatRoom, roomMemberCount);
  }

  @Transactional(readOnly = true)
  public Slice<ChatRoomResponse> getChatRooms(ChatRoomRequest chatRoomRequest, Pageable pageable) {
    Slice<ChatRoomResponse> slicedChatRooms = chatRoomReader.readSlicedChatRooms(chatRoomRequest, pageable);

    return slicedChatRooms;
  }

  @Transactional(readOnly = true)
  public ChatRoomDetails getChatRoomDetails(Long roomId, Long userId) {
    chatRoomUserValidator.checkIsBlockedUser(userId, roomId);

    return chatRoomReader.readChatRoomDetails(roomId, userId);
  }

  @Transactional
  public void reviseChatRoom(ReviseChatRoomRequest request, MultipartFile chatRoomImage, Long userId) {
    ChatRoom chatRoom = chatRoomReader.readChatRoom(userId, request.getRoomId(), HOST);

    if (hasImage(chatRoomImage)) {
      String roomImageUri = storageService.upload(chatRoomImage, UUID.randomUUID().toString(),
          LocalDateTime.now().format(dateTimeFormatter));
      chatRoom = chatRoom.withImageUrl(roomImageUri);
    } else {
      chatRoom = chatRoom.withoutImageUrl();
    }

    if (request.tagExistent()) {
      chatRoomHashTagCleaner.cleanAll(chatRoom.getId());

      List<HashTag> hashTags = request.createHashTags();
      for (HashTag hashTag : hashTags) {
        HashTag storedHashTag = hashTagAppender.append(hashTag);
        chatRoomHashTagAppender.append(chatRoom.getId(), storedHashTag);
      }
    }

    if (request.hasRoomName()) {
      chatRoom = chatRoom.withName(request.getRoomName());
    }

    if (request.canChangeRoomSize(chatRoom.getRoomSize())) {
      chatRoom = chatRoom.withSize(request.getRoomSize());
    }

    chatRoomManager.update(chatRoom);
  }

  @Transactional
  public void enterChatRoom(Long userId, Long roomId) {
    ChatRoom chatRoom = chatRoomReader.readChatRoomWithLock(roomId);
    User user = userReader.readUser(userId);
    participantValidator.checkDoesUserAlreadyParticipate(userId, roomId);
    Host host = participantReader.readHost(roomId);

    if (host.isNotSameUser(user)) {
      chatRoomUserValidator.checkIsBlockedUser(userId, roomId);
      participantValidator.checkIsChatRoomFull(chatRoom);

      Participant participant = Participant.builder()
          .userId(userId)
          .chatRoomId(roomId)
          .status(GUEST)
          .build();

      participantAppender.append(participant);
    }

    Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님이 입장하셨습니다.");

    NotificationMessage entranceMessage = NotificationMessage.createEntranceMessage(chat, userId);
    messagePublisher.sendNotificationMessage(chatRoom.getSid(), entranceMessage);
  }

  @Transactional
  public void exitChatRoom(Long userId, Long roomId) {
    Participant participant = participantReader.readParticipant(userId, roomId);
    ChatRoom chatRoom = chatRoomReader.readChatRoom(roomId);
    Host host = participantReader.readHost(roomId);

    if (host.isSameUser(userId)) {
      Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님이 퇴장하셨습니다.");
      participantCleaner.cleanBy(chatRoom);
      messagePublisher.sendNotificationMessage(chatRoom.getSid(), NotificationMessage.createHostExitMessage(chat));
      return;
    }

    Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님이 퇴장하셨습니다.");

    participantCleaner.clean(participant);
    messagePublisher.sendNotificationMessage(chatRoom.getSid(), NotificationMessage.createExitMessage(chat, userId));
  }

  private boolean hasImage(MultipartFile chatRoomImage) {
    return chatRoomImage != null;
  }
}
