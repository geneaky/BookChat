package toy.bookchat.bookchat.domain.chatroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.ChatRoomsResponseSlice;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.response.UserChatRoomsResponseSlice;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.BookRequest;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.service.ChatAppender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.api.dto.response.UserChatRoomDetailResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.service.ParticipantAppender;
import toy.bookchat.bookchat.domain.participant.service.ParticipantCleaner;
import toy.bookchat.bookchat.domain.participant.service.ParticipantReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.domain.storage.ChatRoomStorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.exception.badrequest.chatroom.NotEnoughRoomSizeException;
import toy.bookchat.bookchat.exception.forbidden.chatroom.BlockedUserInChatRoomException;
import toy.bookchat.bookchat.exception.notfound.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

  @Mock
  ChatRepository chatRepository;
  @Mock
  ChatRoomRepository chatRoomRepository;
  @Mock
  ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
  @Mock
  ChatRoomUserValidator chatRoomUserValidator;
  @Mock
  HashTagRepository hashTagRepository;
  @Mock
  ChatRoomHashTagRepository chatRoomHashTagRepository;
  @Mock
  BookReader bookReader;
  @Mock
  UserReader userReader;
  @Mock
  ParticipantRepository participantRepository;
  @Mock
  ParticipantReader participantReader;
  @Mock
  ChatRoomStorageService storageService;
  @Mock
  MessagePublisher messagePublisher;
  @Mock
  private ParticipantCleaner participantCleaner;
  @Mock
  private ParticipantAppender participantAppender;
  @Mock
  private ParticipantValidator participantValidator;
  @Mock
  private ChatAppender chatAppender;
  @Mock
  private ChatRoomReader chatRoomReader;
  @InjectMocks
  private ChatRoomService chatRoomService;


  private static BookRequest getBookRequest() {
    return BookRequest.builder()
        .isbn("123124")
        .title("effective java")
        .authors(List.of("joshua"))
        .publishAt(LocalDate.now())
        .build();
  }

  private static CreateChatRoomRequest getCreateChatRoomRequest(BookRequest bookRequest) {
    return CreateChatRoomRequest.builder()
        .roomSize(3)
        .roomName("test room")
        .hashTags(List.of("java", "joshua"))
        .bookRequest(bookRequest)
        .build();
  }

  @Test
  void 채팅방_이미지가_없을시_채팅방_생성_성공() throws Exception {
    BookRequest bookRequest = getBookRequest();
    CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .id(1L)
        .bookId(1L)
        .roomSid("7D6")
        .roomImageUri("3wVp")
        .build();

    Book book = Book.builder().build();
    given(bookReader.readBook(any())).willReturn(book);
    given(userReader.readUserEntity(anyLong())).willReturn(mock(UserEntity.class));
    given(chatRoomRepository.save(any())).willReturn(chatRoomEntity);

    chatRoomService.createChatRoom(createChatRoomRequest, null, 1L);

    verify(chatRoomRepository).save(any());
    verify(hashTagRepository, times(2)).save(any());
    verify(chatRoomHashTagRepository, times(2)).save(any());
  }

  @Test
  void 채팅방_이미지가_있을시_채팅방_생성_성공() throws Exception {
    BookRequest bookRequest = getBookRequest();
    CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);
    MultipartFile image = mock(MultipartFile.class);

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .id(1L)
        .bookId(1L)
        .roomSid("7D6")
        .roomImageUri("3wVp")
        .build();

    Book book = Book.builder().build();
    given(bookReader.readBook(any())).willReturn(book);
    given(userReader.readUserEntity(anyLong())).willReturn(mock(UserEntity.class));
    given(chatRoomRepository.save(any())).willReturn(chatRoomEntity);

    chatRoomService.createChatRoom(createChatRoomRequest, image, 1L);

    verify(chatRoomRepository).save(any());
    verify(hashTagRepository, times(2)).save(any());
    verify(chatRoomHashTagRepository, times(2)).save(any());
    verify(storageService).upload(any(), any(), any());

  }

  @Test
  void 등록되지_않은_책으로_채팅방_생성시_책을_등록_후_생성한다() throws Exception {
    BookRequest bookRequest = getBookRequest();
    CreateChatRoomRequest createChatRoomRequest = getCreateChatRoomRequest(bookRequest);

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .id(1L)
        .bookId(1L)
        .roomSid("7D6")
        .roomImageUri("3wVp")
        .build();

    Book book = Book.builder().build();

    given(bookReader.readBook(any())).willReturn(book);
    given(userReader.readUserEntity(anyLong())).willReturn(mock(UserEntity.class));
    given(chatRoomRepository.save(any())).willReturn(chatRoomEntity);

    chatRoomService.createChatRoom(createChatRoomRequest, null, 1L);

    verify(chatRoomRepository).save(any());
    verify(hashTagRepository, times(2)).save(any());
    verify(chatRoomHashTagRepository, times(2)).save(any());
  }

  @Test
  void 사용자_채팅방_조회_성공() throws Exception {
    ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
        .id(1L)
        .roomName("이펙티브 자바 부수는 방")
        .roomSid("secret1")
        .roomSize(100)
        .defaultRoomImageType(1)
        .roomImageUri(null)
        .build();
    chatRoomEntity1.setCreatedAt(LocalDateTime.now());
    ChatEntity chatEntity1 = ChatEntity.builder()
        .id(1L)
        .message("안녕")
        .chatRoomId(chatRoomEntity1.getId())
        .build();
    chatEntity1.setCreatedAt(LocalDateTime.now());
    UserChatRoomResponse userChatRoomResponse = UserChatRoomResponse.builder()
        .roomId(chatRoomEntity1.getId())
        .roomSid(chatRoomEntity1.getRoomSid())
        .roomName(chatRoomEntity1.getRoomName())
        .roomMemberCount(1L)
        .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
        .build();
    List<UserChatRoomResponse> result = List.of(userChatRoomResponse);
    PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
    Slice<UserChatRoomResponse> slice = new SliceImpl<>(result, pageRequest, true);
    when(chatRoomRepository.findUserChatRoomsWithLastChat(any(), any(), any(), any())).thenReturn(slice);
    UserChatRoomsResponseSlice userChatRoomsResponseSlice = chatRoomService.getUserChatRooms(any(), any(), any(),
        any());

    assertThat(userChatRoomsResponseSlice).usingRecursiveComparison().isEqualTo(UserChatRoomsResponseSlice.of(slice));
  }

  @Test
  void 채팅방_조회_성공() throws Exception {
    ChatRoomResponse chatRoomResponse1 = ChatRoomResponse.builder()
        .roomId(1L)
        .roomSid("Dhb")
        .roomName("WLMRXZ")
        .roomMemberCount(3L)
        .roomImageUri("n8QpVmc")
        .bookTitle("book1")
        .bookAuthors(List.of("author1", "author2", "author3"))
        .bookCoverImageUri("book1CoverImage@s3")
        .hostId(1L)
        .hostName("host1")
        .hostProfileImageUri("host1ProfileImage@s3")
        .defaultRoomImageType(1)
        .lastChatSenderId(1L)
        .lastChatId(1L)
        .lastChatMessage("lastChatMessage")
        .tags("tag1,tag2,tag3")
        .lastChatDispatchTime(LocalDateTime.now())
        .build();
    ChatRoomResponse chatRoomResponse2 = ChatRoomResponse.builder()
        .roomId(2L)
        .roomSid("1vaaPp")
        .roomName("R501")
        .roomImageUri("7jutu0i0")
        .roomMemberCount(100L)
        .bookTitle("book2")
        .bookAuthors(List.of("author4", "author5", "author6"))
        .bookCoverImageUri("book2CoverImage@s3")
        .hostId(2L)
        .hostName("host2")
        .hostProfileImageUri("host2ProfileImage@s3")
        .defaultRoomImageType(3)
        .lastChatSenderId(2L)
        .lastChatId(2L)
        .lastChatMessage("lastChatMessage2")
        .tags("tag4,tag2,tag3")
        .lastChatDispatchTime(LocalDateTime.now())
        .build();
    ChatRoomResponse chatRoomResponse3 = ChatRoomResponse.builder()
        .roomId(3L)
        .roomSid("3YzLGXR7")
        .roomName("86H8735E")
        .roomMemberCount(1000L)
        .roomImageUri("sUzZNOV")
        .bookTitle("book3")
        .bookAuthors(List.of("author7", "author8", "author9"))
        .bookCoverImageUri("book3CoverImage@s3")
        .hostId(3L)
        .hostName("host3")
        .hostProfileImageUri("host3ProfileImage@s3")
        .defaultRoomImageType(2)
        .lastChatSenderId(3L)
        .lastChatId(4L)
        .lastChatMessage("lastChatMessage3")
        .tags("tag1,tag5,tag6")
        .lastChatDispatchTime(LocalDateTime.now())
        .build();

    List<ChatRoomResponse> contents = List.of(chatRoomResponse1, chatRoomResponse2,
        chatRoomResponse3);

    Pageable pageable = PageRequest.of(0, 3);

    Slice<ChatRoomResponse> chatRoomResponses = new SliceImpl<>(contents, pageable, true);
    when(chatRoomRepository.findChatRooms(any(), any(), any())).thenReturn(chatRoomResponses);
    ChatRoomsResponseSlice result = chatRoomService.getChatRooms(1L, mock(ChatRoomRequest.class), mock(Pageable.class));

    assertThat(result).isEqualTo(ChatRoomsResponseSlice.of(chatRoomResponses));
  }

  @Test
  void 차단당한_사용자라면_세부정보_조회_실패() throws Exception {
    given(chatRoomBlockedUserRepository.findByUserIdAndChatRoomId(any(), any())).willReturn(
        Optional.ofNullable(mock(ChatRoomBlockedUserEntity.class)));
    assertThatThrownBy(() -> chatRoomService.getChatRoomDetails(1L, 1L)).isInstanceOf(
        BlockedUserInChatRoomException.class);
  }

  @Test
  void 이미_폭파된_방의_세부정보_조회_실패() throws Exception {
    assertThatThrownBy(() -> chatRoomService.getChatRoomDetails(1L, 1L)).isInstanceOf(ChatRoomNotFoundException.class);
  }

  @Test
  void 채팅방_세부정보_조회_성공() throws Exception {
    given(chatRoomRepository.findById(any())).willReturn(Optional.ofNullable(mock(ChatRoomEntity.class)));
    chatRoomService.getChatRoomDetails(1L, 1L);
    verify(chatRoomRepository).findChatRoomDetails(any(), any());
  }

  @Test
  void 채팅방_이미지를_포함한_정보_수정() throws Exception {
    ReviseChatRoomRequest reviseChatRoomRequest = ChatRoomServiceTestFixture.createReviseChatRoomRequest(
        100);
    ChatRoomEntity chatRoomEntity = ChatRoomServiceTestFixture.getChatRoom();
    MockMultipartFile chatRoomImage = new MockMultipartFile("newImageFile", "newImageFile",
        "image/webp", "content".getBytes());

    when(chatRoomRepository.findChatRoomByIdAndHostId(any(), any())).thenReturn(
        Optional.of(chatRoomEntity));
    when(storageService.upload(eq(chatRoomImage), anyString(), anyString())).thenReturn(
        "newRoomImageUri");

    chatRoomService.reviseChatRoom(reviseChatRoomRequest, chatRoomImage, 763L);

    assertThat(chatRoomEntity.getRoomImageUri()).isEqualTo("newRoomImageUri");
  }

  @Test
  void 채팅방_이미지를_제외한_정보_수정() throws Exception {
    ReviseChatRoomRequest chatRoomRequest = ChatRoomServiceTestFixture.createReviseChatRoomRequest(
        100);

    ChatRoomEntity chatRoomEntity = ChatRoomServiceTestFixture.getChatRoom();

    when(chatRoomRepository.findChatRoomByIdAndHostId(any(), any())).thenReturn(
        Optional.of(chatRoomEntity));

    chatRoomService.reviseChatRoom(chatRoomRequest, null, 125L);

    assertThat(chatRoomEntity).extracting(ChatRoomEntity::getRoomName, ChatRoomEntity::getRoomSize)
        .containsExactly(chatRoomRequest.getRoomName(), chatRoomRequest.getRoomSize());
  }

  @Test
  void 채팅방_수정시_현재_방_크기보다_작은_크기로_변경시_예외발생() throws Exception {
    ReviseChatRoomRequest chatRoomRequest = ChatRoomServiceTestFixture.createReviseChatRoomRequest(
        50);

    ChatRoomEntity chatRoomEntity = ChatRoomServiceTestFixture.getChatRoom();

    when(chatRoomRepository.findChatRoomByIdAndHostId(any(), any())).thenReturn(
        Optional.of(chatRoomEntity));

    assertThatThrownBy(() -> {
      chatRoomService.reviseChatRoom(chatRoomRequest, null, 125L);
    }).isInstanceOf(NotEnoughRoomSizeException.class);
  }

  @Test
  @DisplayName("채팅방 입장 성공")
  void enterChatRoom1() throws Exception {
    User user = User.builder().id(1L).build();
    given(userReader.readUser(any())).willReturn(user);
    ChatRoom chatRoom = ChatRoom.builder().id(1L).hostId(2L).build();
    given(chatRoomReader.readChatRoomWithLock(anyLong())).willReturn(chatRoom);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    chatRoomService.enterChatRoom(1L, chatRoom.getId());

    assertAll(
        () -> verify(participantAppender).append(any()),
        () -> verify(messagePublisher).sendNotificationMessage(any(), any())
    );
  }

  @Test
  @DisplayName("게스트 or 부방장이 채팅방을 나갈 수 있다")
  void exitChatRoom1() throws Exception {
    Participant participant = Participant.builder().build();
    given(participantReader.readParticipant(any(), any())).willReturn(participant);
    ChatRoom chatRoom = ChatRoom.builder().build();
    given(chatRoomReader.readChatRoom(any())).willReturn(chatRoom);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    chatRoomService.exitChatRoom(1L, 1L);

    verify(participantCleaner).clean(any());
  }

  @Test
  @DisplayName("방장이 채팅방을 나갈 수 있다")
  void exitChatRoom2() throws Exception {
    Participant participant = Participant.builder().build();
    given(participantReader.readParticipant(any(), any())).willReturn(participant);
    ChatRoom chatRoom = ChatRoom.builder().hostId(1L).build();
    given(chatRoomReader.readChatRoom(any())).willReturn(chatRoom);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    chatRoomService.exitChatRoom(1L, 1L);

    verify(participantCleaner).cleanBy(any());
  }

  @Test
  void 사용자가_채팅방에_참여자가_아닌경우_채팅방_상세정보_조회_실패() throws Exception {
    assertThatThrownBy(() -> chatRoomService.getUserChatRoomDetails(1L, 1L)).isInstanceOf(
        ChatRoomNotFoundException.class);
  }

  @Test
  void 사용자_채팅방_상세정보_조회_성공() throws Exception {
    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .id(1L)
        .roomSid("WGmILQSqkZ")
        .roomName("Xo07yxaT")
        .roomImageUri("oTsF1I1bAR")
        .defaultRoomImageType(1)
        .build();

    given(chatRoomRepository.findUserChatRoom(any(), any())).willReturn(Optional.of(chatRoomEntity));
    given(participantRepository.countByChatRoomId(any())).willReturn(10L);

    UserChatRoomDetailResponse userChatRoomDetailResponse = chatRoomService.getUserChatRoomDetails(1L, 1L);

    assertThat(userChatRoomDetailResponse).extracting(
        UserChatRoomDetailResponse::getRoomId,
        UserChatRoomDetailResponse::getRoomName,
        UserChatRoomDetailResponse::getRoomSid,
        UserChatRoomDetailResponse::getRoomMemberCount,
        UserChatRoomDetailResponse::getRoomImageUri,
        UserChatRoomDetailResponse::getDefaultRoomImageType
    ).containsExactly(
        chatRoomEntity.getId(),
        chatRoomEntity.getRoomName(),
        chatRoomEntity.getRoomSid(),
        10L,
        chatRoomEntity.getRoomImageUri(),
        chatRoomEntity.getDefaultRoomImageType()
    );
  }

  private static class ChatRoomServiceTestFixture {

    public static ReviseChatRoomRequest createReviseChatRoomRequest(Integer roomSize) {
      return ReviseChatRoomRequest.builder()
          .roomId(1L)
          .roomName("afterRoomName")
          .roomSize(roomSize)
          .tags(List.of("tag4", "tag5"))
          .build();
    }

    public static ChatRoomEntity getChatRoom() {
      return ChatRoomEntity.builder()
          .roomName("beforeRoomName")
          .roomSize(100)
          .build();
    }
  }
}