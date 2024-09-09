package toy.bookchat.bookchat.domain.chatroom.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.BookRequest;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.service.ChatAppender;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.HashTags;
import toy.bookchat.bookchat.domain.chatroom.UserChatRoomDetail;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ReviseChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.Host;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.service.ParticipantAppender;
import toy.bookchat.bookchat.domain.participant.service.ParticipantCleaner;
import toy.bookchat.bookchat.domain.participant.service.ParticipantReader;
import toy.bookchat.bookchat.domain.participant.service.ParticipantValidator;
import toy.bookchat.bookchat.domain.storage.ChatRoomStorageService;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

  @Mock
  private ChatRoomUserValidator chatRoomUserValidator;
  @Mock
  private BookReader bookReader;
  @Mock
  private UserReader userReader;
  @Mock
  private ParticipantReader participantReader;
  @Mock
  private ChatRoomStorageService storageService;
  @Mock
  private MessagePublisher messagePublisher;
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
  @Mock
  private ChatRoomManager chatRoomManager;
  @Mock
  private ChatRoomAppender chatRoomAppender;
  @Mock
  private HashTagAppender hashTagAppender;
  @Mock
  private ChatRoomHashTagAppender chatRoomHashTagAppender;
  @Mock
  private ChatRoomHashTagCleaner chatRoomHashTagCleaner;
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
    Book book = Book.builder().build();
    given(bookReader.readBook(any())).willReturn(book);
    given(userReader.readUser(anyLong())).willReturn(mock(User.class));

    BookRequest bookRequest = getBookRequest();
    CreateChatRoomRequest request = getCreateChatRoomRequest(bookRequest);
    ChatRoom chatRoom = request.toChatRoom();
    HashTags hashTags = request.toHashTags();

    chatRoomService.createChatRoom(chatRoom, hashTags, book, null, 1L);

    verify(chatRoomAppender).append(any());
    verify(hashTagAppender, times(2)).append(any());
    verify(chatRoomHashTagAppender, times(2)).append(any(), any());
  }

  @Test
  void 채팅방_이미지가_있을시_채팅방_생성_성공() throws Exception {
    Book book = Book.builder().build();
    given(bookReader.readBook(any())).willReturn(book);
    given(userReader.readUser(anyLong())).willReturn(mock(User.class));

    BookRequest bookRequest = getBookRequest();
    CreateChatRoomRequest request = getCreateChatRoomRequest(bookRequest);
    ChatRoom chatRoom = request.toChatRoom();
    HashTags hashTags = request.toHashTags();
    MultipartFile image = mock(MultipartFile.class);

    chatRoomService.createChatRoom(chatRoom, hashTags, book, image, 1L);

    verify(chatRoomAppender).append(any());
    verify(hashTagAppender, times(2)).append(any());
    verify(chatRoomHashTagAppender, times(2)).append(any(), any());
    verify(storageService).upload(any(), any(), any());
  }

  @Test
  void 사용자_채팅방_조회_성공() throws Exception {
    PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
    chatRoomService.getUserChatRooms(1L, 1L, pageRequest, 1L);

    verify(chatRoomReader).readSliceUserChatRooms(any(), any(), any(), any());
  }

  @Test
  @DisplayName("채팅방 조회 성공")
  void 채팅방_조회_성공() throws Exception {
    ChatRoomRequest request = ChatRoomRequest.builder().build();
    chatRoomService.getChatRooms(request, mock(Pageable.class));

    verify(chatRoomReader).readSlicedChatRooms(any(), any());
  }

  @Test
  @DisplayName("채팅방 세부정보 조회 성공")
  void getChatRoomDetails() throws Exception {
    chatRoomService.getChatRoomDetails(1L, 1L);
    verify(chatRoomReader).readChatRoomDetails(any(), any());
  }

  @Test
  @DisplayName("채팅방 이미지 수정")
  void reviseChatRoom1() throws Exception {
    ChatRoom chatRoom = ChatRoom.builder().roomSize(1).build();
    given(chatRoomReader.readChatRoom(any(), any(), any())).willReturn(chatRoom);
    ReviseChatRoomRequest reviseChatRoomRequest = ReviseChatRoomRequest.builder().roomSize(3).build();
    MockMultipartFile chatRoomImage = new MockMultipartFile("newImageFile", "newImageFile",
        "image/webp", "content".getBytes());

    when(storageService.upload(eq(chatRoomImage), anyString(), anyString())).thenReturn(
        "newRoomImageUri");

    chatRoomService.reviseChatRoom(reviseChatRoomRequest, chatRoomImage, 763L);

    verify(storageService).upload(any(), any(), any());
  }

  @Test
  @DisplayName("채팅방 이미지 제외 수정")
  void reviseChatRoom2() throws Exception {
    ChatRoom chatRoom = ChatRoom.builder().roomSize(1).build();
    given(chatRoomReader.readChatRoom(any(), any(), any())).willReturn(chatRoom);
    ReviseChatRoomRequest reviseChatRoomRequest = ReviseChatRoomRequest.builder().roomSize(3).build();
    chatRoomService.reviseChatRoom(reviseChatRoomRequest, null, 125L);
  }

  @Test
  @DisplayName("채팅방 입장 성공")
  void enterChatRoom1() throws Exception {
    User user = User.builder().id(1L).build();
    given(userReader.readUser(any())).willReturn(user);
    ChatRoom chatRoom = ChatRoom.builder().id(1L).build();
    given(chatRoomReader.readChatRoomWithLock(anyLong())).willReturn(chatRoom);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    Host host = Host.builder().build();
    given(participantReader.readHost(any())).willReturn(host);
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
    Host host = Host.builder().build();
    given(participantReader.readHost(any())).willReturn(host);
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
    ChatRoom chatRoom = ChatRoom.builder().build();
    given(chatRoomReader.readChatRoom(any())).willReturn(chatRoom);
    Host host = Host.builder().userId(1L).build();
    given(participantReader.readHost(any())).willReturn(host);
    Chat chat = Chat.builder().dispatchTime(LocalDateTime.now()).build();
    given(chatAppender.appendAnnouncement(any(), any())).willReturn(chat);

    chatRoomService.exitChatRoom(1L, 1L);

    verify(participantCleaner).cleanBy(any());
  }

  @Test
  @DisplayName("사용자 채팅방 상세정보 조회 성공")
  void getUserChatRoomDetails() throws Exception {
    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .name("roomName")
        .sid("roomSid")
        .roomImageUri("roomImageUri")
        .defaultRoomImageType(1)
        .build();
    given(chatRoomReader.readChatRoom(any(), any())).willReturn(chatRoom);
    given(participantReader.readParticipantCount(any())).willReturn(10L);

    UserChatRoomDetail userChatRoomDetail = chatRoomService.getUserChatRoomDetails(1L, 1L);

    assertThat(userChatRoomDetail).extracting(
        UserChatRoomDetail::getRoomId,
        UserChatRoomDetail::getRoomName,
        UserChatRoomDetail::getRoomSid,
        UserChatRoomDetail::getRoomMemberCount,
        UserChatRoomDetail::getRoomImageUri,
        UserChatRoomDetail::getDefaultRoomImageType
    ).containsExactly(
        chatRoom.getId(),
        chatRoom.getName(),
        chatRoom.getSid(),
        10L,
        chatRoom.getRoomImageUri(),
        chatRoom.getDefaultRoomImageType()
    );
  }
}