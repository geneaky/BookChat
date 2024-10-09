package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomHashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.HashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomBlockedUserRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomHashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomParticipantModel;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.ChatRoomResponse;
import toy.bookchat.bookchat.db_module.chatroom.repository.query.dto.UserChatRoomResponse;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.chatroom.api.v1.response.ChatRoomDetails;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

class ChatRoomEntityRepositoryTest extends RepositoryTest {

  @Autowired
  private ChatRepository chatRepository;
  @Autowired
  private ChatRoomRepository chatRoomRepository;
  @Autowired
  private HashTagRepository hashTagRepository;
  @Autowired
  private ChatRoomHashTagRepository chatRoomHashTagRepository;
  @Autowired
  private ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
  @Autowired
  private ParticipantRepository participantRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("채팅방 저장 성공")
  void save() throws Exception {
    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .bookId(1L)
        .roomName("test room")
        .roomSize(5)
        .roomSid("test sid")
        .defaultRoomImageType(1)
        .build();

    ChatRoomEntity findChatRoomEntity = chatRoomRepository.save(chatRoomEntity);

    assertThat(findChatRoomEntity).isEqualTo(chatRoomEntity);
  }

  @Test
  @DisplayName("사용자의 채팅방 조회시 cursor없는 경우 최근 채팅방순 조회 성공")
  void findUserChatRoomsWithLastChat1() throws Exception {
    UserEntity userEntity1 = UserEntity.builder()
        .nickname("user1")
        .profileImageUrl("user1 image")
        .defaultProfileImageType(1)
        .build();
    UserEntity userEntity2 = UserEntity.builder()
        .nickname("user2")
        .profileImageUrl("user2 image")
        .defaultProfileImageType(2)
        .build();
    userRepository.saveAll(List.of(userEntity1, userEntity2));

    BookEntity bookEntity1 = BookEntity.builder()
        .isbn("1234")
        .title("book1")
        .authors(List.of("author1", "author2", "author3"))
        .publishAt(LocalDate.now())
        .build();
    BookEntity bookEntity2 = BookEntity.builder()
        .isbn("12345")
        .title("book2")
        .authors(List.of("author4", "author5", "author6"))
        .publishAt(LocalDate.now())
        .build();
    bookRepository.saveAll(List.of(bookEntity1, bookEntity2));

    ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
        .bookId(bookEntity1.getId())
        .roomSid("KlV8")
        .roomSize(576)
        .defaultRoomImageType(1)
        .build();
    ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
        .bookId(bookEntity2.getId())
        .roomSid("IwZrRxR5")
        .roomSize(110)
        .defaultRoomImageType(2)
        .build();
    chatRoomRepository.saveAll(List.of(chatRoomEntity1, chatRoomEntity2));

    ParticipantEntity participantEntity1 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity2 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(SUBHOST)
        .build();

    ParticipantEntity participantEntity3 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity4 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(GUEST)
        .build();

    participantRepository.saveAll(
        List.of(participantEntity1, participantEntity2, participantEntity3, participantEntity4));

    ChatEntity chatEntity1 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity1.getId())
        .message("first chat in chatRoom1")
        .build();
    ChatEntity chatEntity2 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity2.getId())
        .message("second chat in chatRoom1")
        .build();
    ChatEntity chatEntity3 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity2.getId())
        .userId(userEntity2.getId())
        .message("first chat in chatRoom2")
        .build();
    ChatEntity chatEntity4 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity2.getId())
        .userId(userEntity1.getId())
        .message("second chat in chatRoom2")
        .build();
    chatRepository.saveAll(List.of(chatEntity1, chatEntity2, chatEntity3, chatEntity4));

    PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
    Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, null, null,
        userEntity1.getId());

    assertThat(slice.getContent()).hasSize(2)
        .extracting(
            UserChatRoomResponse::getRoomId,
            UserChatRoomResponse::getRoomMemberCount,
            UserChatRoomResponse::getHostId,
            UserChatRoomResponse::getSenderId,
            UserChatRoomResponse::getLastChatId,
            UserChatRoomResponse::getBookTitle
        ).containsExactlyInAnyOrder(
            tuple(
                chatRoomEntity1.getId(),
                2L,
                userEntity1.getId(),
                userEntity2.getId(),
                chatEntity2.getId(),
                bookEntity1.getTitle()
            ),
            tuple(
                chatRoomEntity2.getId(),
                2L,
                userEntity2.getId(),
                userEntity1.getId(),
                chatEntity4.getId(),
                bookEntity2.getTitle()
            )
        );
  }

  @Test
  @DisplayName("사용자의 채팅방 조회시 cursor가 있는 경우 최근 채팅순 조회 성공")
  void findUserChatRoomsWithLastChat2() throws Exception {
    UserEntity userEntity1 = UserEntity.builder()
        .nickname("user1")
        .profileImageUrl("user1 image")
        .defaultProfileImageType(1)
        .build();
    UserEntity userEntity2 = UserEntity.builder()
        .nickname("user2")
        .profileImageUrl("user2 image")
        .defaultProfileImageType(2)
        .build();
    userRepository.saveAll(List.of(userEntity1, userEntity2));

    BookEntity bookEntity1 = BookEntity.builder()
        .isbn("1234")
        .title("book1")
        .authors(List.of("author1", "author2", "author3"))
        .publishAt(LocalDate.now())
        .build();
    BookEntity bookEntity2 = BookEntity.builder()
        .isbn("12345")
        .title("book2")
        .authors(List.of("author4", "author5", "author6"))
        .publishAt(LocalDate.now())
        .build();
    bookRepository.saveAll(List.of(bookEntity1, bookEntity2));

    ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
        .bookId(bookEntity1.getId())
        .roomSid("KlV8")
        .roomSize(576)
        .defaultRoomImageType(1)
        .build();
    ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
        .bookId(bookEntity2.getId())
        .roomSid("IwZrRxR5")
        .roomSize(110)
        .defaultRoomImageType(2)
        .build();
    chatRoomRepository.saveAll(List.of(chatRoomEntity1, chatRoomEntity2));

    ParticipantEntity participantEntity1 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity2 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(SUBHOST)
        .build();

    ParticipantEntity participantEntity3 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity4 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(GUEST)
        .build();

    participantRepository.saveAll(
        List.of(participantEntity1, participantEntity2, participantEntity3, participantEntity4));

    ChatEntity chatEntity1 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity1.getId())
        .message("first chat in chatRoom1")
        .build();
    ChatEntity chatEntity2 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity2.getId())
        .message("second chat in chatRoom1")
        .build();
    ChatEntity chatEntity3 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity2.getId())
        .userId(userEntity2.getId())
        .message("first chat in chatRoom2")
        .build();
    ChatEntity chatEntity4 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity2.getId())
        .userId(userEntity1.getId())
        .message("second chat in chatRoom2")
        .build();
    chatRepository.saveAll(List.of(chatEntity1, chatEntity2, chatEntity3, chatEntity4));

    PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
    Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, null,
        chatRoomEntity2.getId(),
        userEntity1.getId());

    assertThat(slice.getContent()).hasSize(1)
        .extracting(
            UserChatRoomResponse::getRoomId,
            UserChatRoomResponse::getRoomMemberCount,
            UserChatRoomResponse::getHostId,
            UserChatRoomResponse::getSenderId,
            UserChatRoomResponse::getLastChatId,
            UserChatRoomResponse::getBookTitle
        ).containsExactly(
            tuple(
                chatRoomEntity1.getId(),
                2L,
                userEntity1.getId(),
                userEntity2.getId(),
                chatEntity2.getId(),
                bookEntity1.getTitle()
            )
        );
  }

  @Test
  @DisplayName("사용자 채팅방 조회 책 ID가 있는 경우 연관된 채팅방만 조회 성공")
  void findUserChatRoomsWithLastChat3() throws Exception {
    UserEntity userEntity1 = UserEntity.builder()
        .nickname("user1")
        .profileImageUrl("user1 image")
        .defaultProfileImageType(1)
        .build();
    UserEntity userEntity2 = UserEntity.builder()
        .nickname("user2")
        .profileImageUrl("user2 image")
        .defaultProfileImageType(2)
        .build();
    userRepository.saveAll(List.of(userEntity1, userEntity2));

    BookEntity bookEntity1 = BookEntity.builder()
        .isbn("1234")
        .title("book1")
        .authors(List.of("author1", "author2", "author3"))
        .publishAt(LocalDate.now())
        .build();
    BookEntity bookEntity2 = BookEntity.builder()
        .isbn("12345")
        .title("book2")
        .authors(List.of("author4", "author5", "author6"))
        .publishAt(LocalDate.now())
        .build();
    bookRepository.saveAll(List.of(bookEntity1, bookEntity2));

    ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
        .bookId(bookEntity1.getId())
        .roomSid("KlV8")
        .roomSize(576)
        .defaultRoomImageType(1)
        .build();
    ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
        .bookId(bookEntity2.getId())
        .roomSid("IwZrRxR5")
        .roomSize(110)
        .defaultRoomImageType(2)
        .build();
    chatRoomRepository.saveAll(List.of(chatRoomEntity1, chatRoomEntity2));

    ParticipantEntity participantEntity1 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity2 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(SUBHOST)
        .build();

    ParticipantEntity participantEntity3 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity4 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(GUEST)
        .build();

    participantRepository.saveAll(
        List.of(participantEntity1, participantEntity2, participantEntity3, participantEntity4));

    ChatEntity chatEntity1 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity1.getId())
        .message("first chat in chatRoom1")
        .build();
    ChatEntity chatEntity2 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity2.getId())
        .message("second chat in chatRoom1")
        .build();
    ChatEntity chatEntity3 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity2.getId())
        .userId(userEntity2.getId())
        .message("first chat in chatRoom2")
        .build();
    ChatEntity chatEntity4 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity2.getId())
        .userId(userEntity1.getId())
        .message("second chat in chatRoom2")
        .build();
    chatRepository.saveAll(List.of(chatEntity1, chatEntity2, chatEntity3, chatEntity4));

    PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
    Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest,
        bookEntity2.getId(), null, userEntity1.getId());

    assertThat(slice.getContent()).hasSize(1)
        .extracting(
            UserChatRoomResponse::getRoomId,
            UserChatRoomResponse::getRoomMemberCount,
            UserChatRoomResponse::getHostId,
            UserChatRoomResponse::getSenderId,
            UserChatRoomResponse::getLastChatId,
            UserChatRoomResponse::getBookTitle
        ).containsExactlyInAnyOrder(
            tuple(
                chatRoomEntity2.getId(),
                2L,
                userEntity2.getId(),
                userEntity1.getId(),
                chatEntity4.getId(),
                bookEntity2.getTitle()
            )
        );
  }

  @Test
  @DisplayName("채팅방 조회 성공")
  void findChatRooms() throws Exception {
    UserEntity userEntity1 = UserEntity.builder()
        .nickname("nickname1")
        .profileImageUrl("profileImageUrl1")
        .defaultProfileImageType(1)
        .build();
    UserEntity userEntity2 = UserEntity.builder()
        .nickname("nickname2")
        .profileImageUrl("profileImageUrl2")
        .defaultProfileImageType(2)
        .build();
    userRepository.saveAll(List.of(userEntity1, userEntity2));

    BookEntity bookEntity = BookEntity.builder()
        .title("가나다 라마 바사")
        .isbn("773898468")
        .authors(List.of("author1", "author2"))
        .bookCoverImageUrl("bookCoverImage")
        .publishAt(LocalDate.now())
        .build();
    bookRepository.save(bookEntity);

    ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
        .bookId(bookEntity.getId())
        .roomName("chatRoom1")
        .roomSid("chatRoom1")
        .roomSize(77)
        .defaultRoomImageType(1)
        .build();
    ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
        .bookId(bookEntity.getId())
        .roomName("chatRoom2")
        .roomSid("chatRoom2")
        .roomSize(77)
        .defaultRoomImageType(1)
        .build();
    chatRoomRepository.saveAll(List.of(chatRoomEntity1, chatRoomEntity2));

    HashTagEntity tag1 = HashTagEntity.of("hashTag1");
    HashTagEntity tag2 = HashTagEntity.of("hashTag2");
    HashTagEntity tag3 = HashTagEntity.of("hashTag3");
    hashTagRepository.saveAll(List.of(tag1, tag2, tag3));

    ChatRoomHashTagEntity chatRoomHashTagEntity1 = ChatRoomHashTagEntity.of(chatRoomEntity1.getId(), tag1.getId());
    ChatRoomHashTagEntity chatRoomHashTagEntity2 = ChatRoomHashTagEntity.of(chatRoomEntity2.getId(), tag2.getId());
    ChatRoomHashTagEntity chatRoomHashTagEntity3 = ChatRoomHashTagEntity.of(chatRoomEntity1.getId(), tag3.getId());
    chatRoomHashTagRepository.saveAll(List.of(chatRoomHashTagEntity1, chatRoomHashTagEntity2, chatRoomHashTagEntity3));

    ParticipantEntity participantEntity1 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(HOST).build();
    ParticipantEntity participantEntity2 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(GUEST).build();
    ParticipantEntity participantEntity3 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(HOST).build();
    participantRepository.saveAll(List.of(participantEntity1, participantEntity2, participantEntity3));

    ChatEntity chatEntity1 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity1.getId())
        .build();
    ChatEntity chatEntity2 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity2.getId())
        .build();
    ChatEntity chatEntity3 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity2.getId())
        .userId(userEntity2.getId())
        .build();
    ChatEntity chatEntity4 = ChatEntity.builder()
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity1.getId())
        .message("test chat5")
        .build();
    chatRepository.saveAll(List.of(chatEntity1, chatEntity2, chatEntity3, chatEntity4));

    PageRequest pageable = PageRequest.of(0, 1);
    ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
        .postCursorId(500L)
        .tags(List.of("hashTag1"))
        .build();

    Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(chatRoomRequest, pageable);

    ChatRoomResponse expect = ChatRoomResponse.builder()
        .roomId(chatRoomEntity1.getId())
        .roomName(chatRoomEntity1.getRoomName())
        .roomSid(chatRoomEntity1.getRoomSid())
        .roomImageUri(chatRoomEntity1.getRoomImageUri())
        .roomMemberCount(2L)
        .roomSize(chatRoomEntity1.getRoomSize())
        .defaultRoomImageType(chatRoomEntity1.getDefaultRoomImageType())
        .bookTitle(bookEntity.getTitle())
        .bookCoverImageUri(bookEntity.getBookCoverImageUrl())
        .bookAuthors(bookEntity.getAuthors())
        .hostId(userEntity1.getId())
        .hostName(userEntity1.getNickname())
        .hostDefaultProfileImageType(userEntity1.getDefaultProfileImageType())
        .hostProfileImageUri(userEntity1.getProfileImageUrl())
        .tags("hashTag1")
        .lastChatSenderId(chatEntity4.getUserId())
        .lastChatId(chatEntity4.getId())
        .lastChatMessage(chatEntity4.getMessage())
        .lastChatDispatchTime(chatEntity4.getCreatedAt())
        .build();

    assertThat(result.getContent()).isEqualTo(List.of(expect));
  }

  @Test
  @DisplayName("채팅방 세부정보 조회 성공")
  void findChatRoomDetails1() throws Exception {
    UserEntity userEntity1 = UserEntity.builder()
        .nickname("AUser")
        .defaultProfileImageType(1)
        .build();
    UserEntity userEntity2 = UserEntity.builder()
        .nickname("BUser")
        .profileImageUrl("testB@s3.com")
        .defaultProfileImageType(1)
        .build();
    UserEntity userEntity3 = UserEntity.builder()
        .nickname("CUser")
        .profileImageUrl("testC@s3.com")
        .defaultProfileImageType(1)
        .build();
    userRepository.saveAll(List.of(userEntity1, userEntity2, userEntity3));

    BookEntity bookEntity = BookEntity.builder()
        .title("effectiveJava")
        .isbn("tXaid")
        .publishAt(LocalDate.now())
        .authors(List.of("joshua", "jcr"))
        .bookCoverImageUrl("effective@s3.com")
        .build();
    bookRepository.save(bookEntity);

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .bookId(bookEntity.getId())
        .roomSid("cES1Cn4N")
        .roomSize(275)
        .defaultRoomImageType(2)
        .build();
    chatRoomRepository.save(chatRoomEntity);

    ChatRoomBlockedUserEntity chatRoomBlockedUserEntity = ChatRoomBlockedUserEntity.builder()
        .chatRoomId(chatRoomEntity.getId())
        .userId(userEntity3.getId())
        .build();
    chatRoomBlockedUserRepository.save(chatRoomBlockedUserEntity);

    HashTagEntity tag = HashTagEntity.of("tag1");
    hashTagRepository.save(tag);

    ChatRoomHashTagEntity chatRoomHashTagEntity = ChatRoomHashTagEntity.of(chatRoomEntity.getId(), tag.getId());
    chatRoomHashTagRepository.save(chatRoomHashTagEntity);

    ParticipantEntity participantEntity1 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .participantStatus(HOST)
        .chatRoomId(chatRoomEntity.getId())
        .build();
    ParticipantEntity participantEntity2 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .participantStatus(SUBHOST)
        .chatRoomId(chatRoomEntity.getId())
        .build();
    ParticipantEntity participantEntity3 = ParticipantEntity.builder()
        .userId(userEntity3.getId())
        .participantStatus(GUEST)
        .chatRoomId(chatRoomEntity.getId())
        .build();
    participantRepository.saveAll(List.of(participantEntity1, participantEntity2, participantEntity3));

    ChatRoomParticipantModel chatRoomParticipantModel1 = new ChatRoomParticipantModel(userEntity1.getId(),
        userEntity1.getNickname(), userEntity1.getProfileImageUrl(),
        userEntity1.getDefaultProfileImageType(), HOST);
    ChatRoomParticipantModel chatRoomParticipantModel2 = new ChatRoomParticipantModel(userEntity2.getId(),
        userEntity2.getNickname(), userEntity2.getProfileImageUrl(),
        userEntity2.getDefaultProfileImageType(), SUBHOST);
    ChatRoomParticipantModel chatRoomParticipantModel3 = new ChatRoomParticipantModel(userEntity3.getId(),
        userEntity3.getNickname(), userEntity3.getProfileImageUrl(),
        userEntity3.getDefaultProfileImageType(), GUEST);
    List<ChatRoomParticipantModel> chatRoomParticipantModels = List.of(chatRoomParticipantModel1,
        chatRoomParticipantModel2, chatRoomParticipantModel3);

    ChatRoomDetails real = chatRoomRepository.findChatRoomDetails(chatRoomEntity.getId(), userEntity3.getId());

    ChatRoomDetails expect = ChatRoomDetails.from(chatRoomParticipantModels, List.of(tag.getTagName()), bookEntity,
        chatRoomEntity);

    assertThat(real).isEqualTo(expect);
  }

  @Test
  @DisplayName("존재하지 않는 채팅방 세부정보 조회시 예외발생")
  void findChatRoomDetails2() throws Exception {
    assertThatThrownBy(() -> {
      chatRoomRepository.findChatRoomDetails(53L, 606L);
    }).isInstanceOf(ParticipantNotFoundException.class);
  }

  @Test
  @DisplayName("사용자가 접속한 채팅방 조회 성공")
  void findUserChatRoom() throws Exception {
    UserEntity userEntity = UserEntity.builder().build();
    userRepository.save(userEntity);

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .bookId(1L)
        .roomSid("4SyVX")
        .roomSize(77)
        .defaultRoomImageType(1)
        .build();
    chatRoomRepository.save(chatRoomEntity);

    ParticipantEntity participantEntity = ParticipantEntity.builder()
        .userId(userEntity.getId())
        .chatRoomId(chatRoomEntity.getId())
        .participantStatus(GUEST)
        .build();
    participantRepository.save(participantEntity);

    Optional<ChatRoomEntity> optionalChatRoom = chatRoomRepository.findUserChatRoom(chatRoomEntity.getId(),
        userEntity.getId(), null);

    assertThat(optionalChatRoom).isPresent();
  }
}