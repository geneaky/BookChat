package toy.bookchat.bookchat.domain.chatroom.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.HashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.service.ChatAppender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.api.dto.response.UserChatRoomDetailResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.response.CreatedChatRoomDto;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.service.ParticipantAppender;
import toy.bookchat.bookchat.domain.participant.service.ParticipantCleaner;
import toy.bookchat.bookchat.domain.participant.service.ParticipantReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.storage.StorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.exception.forbidden.chatroom.BlockedUserInChatRoomException;
import toy.bookchat.bookchat.exception.notfound.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.broker.message.NotificationMessage;

@Service
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ParticipantRepository participantRepository;
  private final HashTagRepository hashTagRepository;
  private final ChatRoomHashTagRepository chatRoomHashTagRepository;
  private final StorageService storageService;
  private final BookReader bookReader;
  private final UserReader userReader;
  private final ChatRoomReader chatRoomReader;
  private final ChatRoomUserValidator chatRoomUserValidator;
  private final ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
  private final MessagePublisher messagePublisher;
  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private final ParticipantAppender participantAppender;
  private final ParticipantValidator participantValidator;
  private final ParticipantCleaner participantCleaner;
  private final ChatAppender chatAppender;
  private final ParticipantReader participantReader;

  public ChatRoomService(ChatRoomRepository chatRoomRepository,
      ParticipantRepository participantRepository, HashTagRepository hashTagRepository,
      ChatRoomHashTagRepository chatRoomHashTagRepository,
      @Qualifier("chatRoomStorageService") StorageService storageService, BookReader bookReader,
      UserReader userReader, ChatRoomReader chatRoomReader, ChatRoomUserValidator chatRoomUserValidator,
      ChatRoomBlockedUserRepository chatRoomBlockedUserRepository,
      MessagePublisher messagePublisher, ParticipantAppender participantAppender,
      ParticipantValidator participantValidator, ParticipantCleaner participantCleaner, ChatAppender chatAppender,
      ParticipantReader participantReader) {
    this.chatRoomRepository = chatRoomRepository;
    this.participantRepository = participantRepository;
    this.hashTagRepository = hashTagRepository;
    this.chatRoomHashTagRepository = chatRoomHashTagRepository;
    this.storageService = storageService;
    this.bookReader = bookReader;
    this.userReader = userReader;
    this.chatRoomReader = chatRoomReader;
    this.chatRoomUserValidator = chatRoomUserValidator;
    this.chatRoomBlockedUserRepository = chatRoomBlockedUserRepository;
    this.messagePublisher = messagePublisher;
    this.participantAppender = participantAppender;
    this.participantValidator = participantValidator;
    this.participantCleaner = participantCleaner;
    this.chatAppender = chatAppender;
    this.participantReader = participantReader;
  }

  @Transactional
  public CreatedChatRoomDto createChatRoom(CreateChatRoomRequest createChatRoomRequest, MultipartFile chatRoomImage,
      Long userId) {
    Book book = bookReader.readBook(createChatRoomRequest.createBook());

    UserEntity host = userReader.readUserEntity(userId);

    if (chatRoomImageExistent(chatRoomImage)) {
      String uploadFileUrl = storageService.upload(chatRoomImage, UUID.randomUUID().toString(),
          LocalDateTime.now().format(dateTimeFormatter));
      return CreatedChatRoomDto.of(registerChatRoom(createChatRoomRequest, book, host, uploadFileUrl));
    }
    return CreatedChatRoomDto.of(registerChatRoom(createChatRoomRequest, book, host, null));
  }

  private boolean chatRoomImageExistent(MultipartFile chatRoomImage) {
    return chatRoomImage != null;
  }

  private ChatRoomEntity registerChatRoom(CreateChatRoomRequest createChatRoomRequest, Book book, UserEntity host,
      String prefixedUUIDFileUrl) {
    ChatRoomEntity chatRoomEntity = saveChatRoom(createChatRoomRequest, book, host, prefixedUUIDFileUrl);
    registerHashTagOnChatRoom(createChatRoomRequest, chatRoomEntity);
    return chatRoomEntity;
  }

  private ChatRoomEntity saveChatRoom(CreateChatRoomRequest createChatRoomRequest, Book book, UserEntity host,
      String fileUrl) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.save(createChatRoomRequest.makeChatRoom(book, host, fileUrl));
    saveParticipantWithRoomHostAndRoom(host, chatRoomEntity);
    return chatRoomEntity;
  }

  private void saveParticipantWithRoomHostAndRoom(UserEntity host, ChatRoomEntity chatRoomEntity) {
    ParticipantEntity participantEntity = ParticipantEntity.builder()
        .participantStatus(HOST)
        .chatRoomId(chatRoomEntity.getId())
        .userId(host.getId())
        .build();
    participantRepository.save(participantEntity);
  }

  private void registerHashTagOnChatRoom(CreateChatRoomRequest createChatRoomRequest, ChatRoomEntity chatRoomEntity) {
    createChatRoomRequest.getHashTags().stream()
        .map(tagName -> hashTagRepository.findByTagName(tagName)
            .orElseGet(() -> hashTagRepository.save(HashTagEntity.of(tagName))))
        .forEach(
            hashTag -> chatRoomHashTagRepository.save(ChatRoomHashTagEntity.of(chatRoomEntity, hashTag)));
  }

  @Transactional(readOnly = true)
  public UserChatRoomsResponseSlice getUserChatRooms(Long bookId, Long postCursorId, Pageable pageable, Long userId) {
    return UserChatRoomsResponseSlice.of(
        chatRoomRepository.findUserChatRoomsWithLastChat(pageable, bookId, postCursorId, userId));
  }

  @Transactional(readOnly = true)
  public UserChatRoomDetailResponse getUserChatRoomDetails(Long roomId, Long userId) {
    ChatRoomEntity chatroom = chatRoomRepository.findUserChatRoom(roomId, userId)
        .orElseThrow(ChatRoomNotFoundException::new);
    Long roomMemberCount = participantRepository.countByChatRoomId(chatroom.getId());

    return UserChatRoomDetailResponse.from(chatroom, roomMemberCount);
  }

  @Transactional(readOnly = true)
  public ChatRoomsResponseSlice getChatRooms(Long userId, ChatRoomRequest chatRoomRequest, Pageable pageable) {
    return ChatRoomsResponseSlice.of(chatRoomRepository.findChatRooms(userId, chatRoomRequest, pageable));
  }

  @Transactional(readOnly = true)
  public ChatRoomDetails getChatRoomDetails(Long roomId, Long userId) {
    chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(userId, roomId)
        .ifPresent(b -> {
          throw new BlockedUserInChatRoomException();
        });
    chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);

    return chatRoomRepository.findChatRoomDetails(roomId, userId);
  }

  @Transactional
  public void reviseChatRoom(ReviseChatRoomRequest reviseChatRoomRequest, MultipartFile chatRoomImage, Long userId) {
    ChatRoomEntity chatRoomEntity = chatRoomRepository.findChatRoomByIdAndHostId(reviseChatRoomRequest.getRoomId(),
        userId).orElseThrow(ChatRoomNotFoundException::new);

    updateIfChatRoomHashTagsPresent(reviseChatRoomRequest, chatRoomEntity);
    if (chatRoomImageExistent(chatRoomImage)) {
      String roomImageUri = storageService.upload(chatRoomImage, UUID.randomUUID().toString(),
          LocalDateTime.now().format(dateTimeFormatter));
      chatRoomEntity.changeRoomImageUri(roomImageUri);
    }
    reviseChatRoomRequest.reviseChatRoom(chatRoomEntity);
  }

  private void updateIfChatRoomHashTagsPresent(ReviseChatRoomRequest reviseChatRoomRequest,
      ChatRoomEntity chatRoomEntity) {
    if (reviseChatRoomRequest.tagExistent()) {
      chatRoomHashTagRepository.deleteAllByChatRoomEntity(chatRoomEntity);

      List<HashTagEntity> hashTagEntities = reviseChatRoomRequest.createHashTag();
      hashTagRepository.saveAll(hashTagEntities);

      List<ChatRoomHashTagEntity> chatRoomHashTagEntities = hashTagEntities.stream()
          .map(ht -> ChatRoomHashTagEntity.of(chatRoomEntity, ht))
          .collect(Collectors.toList());
      chatRoomHashTagRepository.saveAll(chatRoomHashTagEntities);
    }
  }

  @Transactional
  public void enterChatRoom(Long userId, Long roomId) {
    ChatRoom chatRoom = chatRoomReader.readChatRoomWithLock(roomId);
    User user = userReader.readUser(userId);
    participantValidator.checkDoesUserAlreadyParticipate(userId, roomId);

    if (chatRoom.isNotHost(user)) {
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

    if (chatRoom.isHost(userId)) {
      Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님이 퇴장하셨습니다.");
      participantCleaner.cleanBy(chatRoom);
      messagePublisher.sendNotificationMessage(chatRoom.getSid(), NotificationMessage.createHostExitMessage(chat));
      return;
    }

    Chat chat = chatAppender.appendAnnouncement(roomId, "#" + userId + "#님이 퇴장하셨습니다.");

    participantCleaner.clean(participant);
    messagePublisher.sendNotificationMessage(chatRoom.getSid(), NotificationMessage.createExitMessage(chat, userId));
  }
}
