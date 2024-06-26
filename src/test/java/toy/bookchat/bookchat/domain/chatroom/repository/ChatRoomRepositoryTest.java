package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chat.repository.ChatRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTag;
import toy.bookchat.bookchat.domain.chatroom.HashTag;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.response.ChatRoomDetails;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@RepositoryTest
class ChatRoomRepositoryTest {

    @Autowired
    ChatRepository chatRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    HashTagRepository hashTagRepository;
    @Autowired
    ChatRoomHashTagRepository chatRoomHashTagRepository;
    @Autowired
    ChatRoomBlockedUserRepository chatRoomBlockedUserRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void 채팅방_저장_성공() throws Exception {
        ChatRoom chatRoom = ChatRoom.builder()
            .roomName("test room")
            .roomSize(5)
            .roomSid("test sid")
            .defaultRoomImageType(1)
            .build();

        ChatRoom findChatRoom = chatRoomRepository.save(chatRoom);

        assertThat(findChatRoom).isEqualTo(chatRoom);
    }

    @Test
    void 사용자의_채팅방_커서없는경우_최근채팅방순_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);

        Book book1 = Book.builder()
            .isbn("12342")
            .authors(List.of("author1", "author2", "author3"))
            .publishAt(LocalDate.now())
            .build();
        Book book2 = Book.builder()
            .isbn("123426")
            .authors(List.of("author4", "author5", "author6"))
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book1);
        bookRepository.save(book2);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book1)
            .host(user1)
            .roomSid("KlV8")
            .roomSize(576)
            .defaultRoomImageType(1)
            .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book2)
            .host(user1)
            .roomSid("IwZrRxR5")
            .roomSize(110)
            .defaultRoomImageType(2)
            .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
            .book(book1)
            .host(user2)
            .roomSid("Gmw9yDI4")
            .roomSize(591)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomRepository.save(chatRoom3);

        ChatRoomBlockedUser chatRoomBlockedUser = ChatRoomBlockedUser.builder()
            .user(user1)
            .chatRoom(chatRoom3)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUser);

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user1).chatRoom(chatRoom2)
            .participantStatus(SUBHOST).build();
        Participant participant3 = Participant.builder().user(user1).chatRoom(chatRoom3)
            .participantStatus(GUEST).build();
        Participant participant4 = Participant.builder().user(user2).chatRoom(chatRoom3)
            .participantStatus(GUEST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);
        participantRepository.save(participant4);

        Chat chat1 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .message("first chat in chatRoom1")
            .build();
        Chat chat2 = Chat.builder()
            .chatRoom(chatRoom2)
            .user(user1)
            .message("first chat in chatRoom2")
            .build();
        Chat chat3 = Chat.builder()
            .chatRoom(chatRoom3)
            .user(user1)
            .message("first chat in chatRoom3")
            .build();
        Chat chat4 = Chat.builder()
            .chatRoom(chatRoom3)
            .user(user2)
            .message("second chat in chatRoom3")
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoom3.getId())
            .defaultRoomImageType(chatRoom3.getDefaultRoomImageType())
            .roomSid(chatRoom3.getRoomSid())
            .roomMemberCount(2L)
            .hostId(user2.getId())
            .hostNickname(user2.getNickname())
            .hostProfileImageUrl(user2.getProfileImageUrl())
            .hostDefaultProfileImageType(user2.getDefaultProfileImageType())
            .bookTitle(chatRoom3.getBookTitle())
            .bookCoverImageUrl(chatRoom3.getBookCoverImageUrl())
            .bookAuthors(chatRoom3.getBookAuthors())
            .senderId(chat4.getUserId())
            .senderNickname(chat4.getUserNickname())
            .senderProfileImageUrl(chat4.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chat4.getUserDefaultProfileImageType())
            .lastChatId(chat4.getId())
            .lastChatContent(chat4.getMessage())
            .lastChatDispatchTime(chat4.getCreatedAt())
            .build();

        UserChatRoomResponse userChatRoomResponse2 = UserChatRoomResponse.builder()
            .roomId(chatRoom2.getId())
            .defaultRoomImageType(chatRoom2.getDefaultRoomImageType())
            .roomSid(chatRoom2.getRoomSid())
            .roomMemberCount(1L)
            .hostId(user1.getId())
            .hostNickname(user1.getNickname())
            .hostProfileImageUrl(user1.getProfileImageUrl())
            .hostDefaultProfileImageType(user1.getDefaultProfileImageType())
            .bookTitle(chatRoom2.getBookTitle())
            .bookCoverImageUrl(chatRoom2.getBookCoverImageUrl())
            .bookAuthors(chatRoom2.getBookAuthors())
            .senderId(chat2.getUserId())
            .senderNickname(chat2.getUserNickname())
            .senderProfileImageUrl(chat2.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chat2.getUserDefaultProfileImageType())
            .lastChatId(chat2.getId())
            .lastChatContent(chat2.getMessage())
            .lastChatDispatchTime(chat2.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        List<UserChatRoomResponse> contents = List.of(userChatRoomResponse1, userChatRoomResponse2);
        Slice<UserChatRoomResponse> result = toSlice(contents, pageRequest);

        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, null, null, user1.getId());

        assertThat(slice.getContent()).usingRecursiveComparison()
            .ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(result.getContent());
    }

    @Test
    void 사용자의_채팅방_커서있는경우_최근채팅순_조회_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);

        Book book1 = Book.builder()
            .isbn("12342")
            .authors(List.of("author1", "author2", "author3"))
            .publishAt(LocalDate.now())
            .build();
        Book book2 = Book.builder()
            .isbn("123426")
            .authors(List.of("author4", "author5", "author6"))
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book1);
        bookRepository.save(book2);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book1)
            .host(user1)
            .roomSid("KlV8")
            .roomSize(576)
            .defaultRoomImageType(1)
            .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book2)
            .host(user1)
            .roomSid("IwZrRxR5")
            .roomSize(110)
            .defaultRoomImageType(2)
            .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
            .book(book1)
            .host(user2)
            .roomSid("Gmw9yDI4")
            .roomSize(591)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomRepository.save(chatRoom3);

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user1).chatRoom(chatRoom2)
            .participantStatus(SUBHOST).build();
        Participant participant3 = Participant.builder().user(user1).chatRoom(chatRoom3)
            .participantStatus(GUEST).build();
        Participant participant4 = Participant.builder().user(user2).chatRoom(chatRoom3)
            .participantStatus(GUEST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);
        participantRepository.save(participant4);

        Chat chat1 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .message("first chat in chatRoom1")
            .build();
        Chat chat2 = Chat.builder()
            .chatRoom(chatRoom2)
            .user(user1)
            .message("first chat in chatRoom2")
            .build();
        Chat chat3 = Chat.builder()
            .chatRoom(chatRoom3)
            .user(user1)
            .message("first chat in chatRoom3")
            .build();
        Chat chat4 = Chat.builder()
            .chatRoom(chatRoom3)
            .user(user2)
            .message("second chat in chatRoom3")
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoom2.getId())
            .defaultRoomImageType(chatRoom2.getDefaultRoomImageType())
            .roomSid(chatRoom2.getRoomSid())
            .roomMemberCount(1L)
            .hostId(user1.getId())
            .hostNickname(user1.getNickname())
            .hostProfileImageUrl(user1.getProfileImageUrl())
            .hostDefaultProfileImageType(user1.getDefaultProfileImageType())
            .bookTitle(chatRoom2.getBookTitle())
            .bookCoverImageUrl(chatRoom2.getBookCoverImageUrl())
            .bookAuthors(chatRoom2.getBookAuthors())
            .senderId(chat2.getUserId())
            .senderNickname(chat2.getUserNickname())
            .senderProfileImageUrl(chat2.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chat2.getUserDefaultProfileImageType())
            .lastChatId(chat2.getId())
            .lastChatContent(chat2.getMessage())
            .lastChatDispatchTime(chat2.getCreatedAt())
            .build();

        UserChatRoomResponse userChatRoomResponse2 = UserChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .roomSid(chatRoom1.getRoomSid())
            .roomMemberCount(1L)
            .hostId(user1.getId())
            .hostNickname(user1.getNickname())
            .hostProfileImageUrl(user1.getProfileImageUrl())
            .hostDefaultProfileImageType(user1.getDefaultProfileImageType())
            .bookTitle(chatRoom1.getBookTitle())
            .bookCoverImageUrl(chatRoom1.getBookCoverImageUrl())
            .bookAuthors(chatRoom1.getBookAuthors())
            .senderId(chat1.getUserId())
            .senderNickname(chat1.getUserNickname())
            .senderProfileImageUrl(chat1.getUserProfileImageUrl())
            .senderDefaultProfileImageType(chat1.getUserDefaultProfileImageType())
            .lastChatId(chat1.getId())
            .lastChatContent(chat1.getMessage())
            .lastChatDispatchTime(chat1.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        List<UserChatRoomResponse> contents = List.of(userChatRoomResponse1, userChatRoomResponse2);

        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, null, chatRoom3.getId(), user1.getId());

        assertThat(slice.getContent()).usingRecursiveComparison()
            .ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(contents);
    }

    @Test
    void 사용자_채팅방_조회_책_id_있는경우_연관된_채팅방만_조회_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.saveAll(List.of(user1, user2));

        Book book1 = Book.builder()
            .isbn("12329763345")
            .publishAt(LocalDate.now())
            .build();
        Book book2 = Book.builder()
            .isbn("12329724525")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.saveAll(List.of(book1, book2));

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book1)
            .host(user1)
            .roomSid("4SyVX")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book2)
            .host(user1)
            .roomSid("1Y2j9RlN")
            .roomSize(573)
            .defaultRoomImageType(2)
            .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
            .book(book2)
            .host(user2)
            .roomSid("r7xr")
            .roomSize(38)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.saveAll(List.of(chatRoom1, chatRoom2, chatRoom3));

        ChatRoomBlockedUser chatRoomBlockedUser = ChatRoomBlockedUser.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUser);

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user1).chatRoom(chatRoom2)
            .participantStatus(GUEST).build();
        Participant participant3 = Participant.builder().user(user1).chatRoom(chatRoom3)
            .participantStatus(SUBHOST).build();
        Participant participant4 = Participant.builder().user(user2).chatRoom(chatRoom3)
            .participantStatus(GUEST).build();
        participantRepository.saveAll(List.of(participant1, participant2, participant3, participant4));

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .roomSid(chatRoom1.getRoomSid())
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .roomMemberCount(1L)
            .hostId(user1.getId())
            .hostNickname(user1.getNickname())
            .hostProfileImageUrl(user1.getProfileImageUrl())
            .hostDefaultProfileImageType(user1.getDefaultProfileImageType())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(pageRequest, book1.getId(), chatRoom3.getId(), user1.getId());

        assertThat(slice.getContent()).containsOnly(userChatRoomResponse1);
    }

    @Test
    void 채팅방_조회_성공() throws Exception {
        User user1 = User.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        User user2 = User.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        User user3 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Book book = Book.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book)
            .host(user2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        HashTag tag1 = HashTag.of("hashTag1");
        HashTag tag2 = HashTag.of("hashTag2");
        HashTag tag3 = HashTag.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag3));

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user2).chatRoom(chatRoom2)
            .participantStatus(HOST).build();
        Participant participant3 = Participant.builder().user(user2).chatRoom(chatRoom1)
            .participantStatus(GUEST).build();
        Participant participant4 = Participant.builder().user(user3).chatRoom(chatRoom2)
            .participantStatus(SUBHOST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);
        participantRepository.save(participant4);

        Chat chat1 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        Chat chat2 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user2)
            .build();
        Chat chat3 = Chat.builder()
            .chatRoom(chatRoom2)
            .user(user2)
            .build();
        Chat chat4 = Chat.builder()
            .chatRoom(chatRoom2)
            .user(user3)
            .build();
        Chat chat5 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .message("test chat5")
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);
        chatRepository.save(chat5);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag1"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(user1.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .roomName(chatRoom1.getRoomName())
            .roomSid(chatRoom1.getRoomSid())
            .roomImageUri(chatRoom1.getRoomImageUri())
            .roomMemberCount(2L)
            .roomSize(chatRoom1.getRoomSize())
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .bookTitle(book.getTitle())
            .bookCoverImageUri(book.getBookCoverImageUrl())
            .bookAuthors(book.getAuthors())
            .hostId(user1.getId())
            .hostName(user1.getNickname())
            .hostDefaultProfileImageType(user1.getDefaultProfileImageType())
            .hostProfileImageUri(user1.getProfileImageUrl())
            .tags("hashTag1,hashTag3")
            .lastChatSenderId(chat5.getUserId())
            .lastChatId(chat5.getId())
            .lastChatMessage(chat5.getMessage())
            .lastChatDispatchTime(chat5.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));
    }

    @Test
    void 참여하지_않은_채팅방_조회_성공() throws Exception {
        User user1 = User.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        User user2 = User.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        User user3 = User.builder()
            .nickname("nickname3")
            .profileImageUrl("profileImageUrl3")
            .defaultProfileImageType(2)
            .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Book book = Book.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book)
            .host(user2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        HashTag tag1 = HashTag.of("hashTag1");
        HashTag tag2 = HashTag.of("hashTag2");
        HashTag tag3 = HashTag.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag3));

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user2).chatRoom(chatRoom2)
            .participantStatus(HOST).build();
        Participant participant3 = Participant.builder().user(user2).chatRoom(chatRoom1)
            .participantStatus(GUEST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);

        Chat chat1 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        Chat chat2 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user2)
            .build();
        Chat chat3 = Chat.builder()
            .chatRoom(chatRoom2)
            .user(user2)
            .build();
        Chat chat4 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .message("test chat5")
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag1"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(user3.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .roomName(chatRoom1.getRoomName())
            .roomSid(chatRoom1.getRoomSid())
            .roomImageUri(chatRoom1.getRoomImageUri())
            .roomMemberCount(2L)
            .roomSize(chatRoom1.getRoomSize())
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .bookTitle(book.getTitle())
            .bookCoverImageUri(book.getBookCoverImageUrl())
            .bookAuthors(book.getAuthors())
            .hostId(user1.getId())
            .hostName(user1.getNickname())
            .hostDefaultProfileImageType(user1.getDefaultProfileImageType())
            .hostProfileImageUri(user1.getProfileImageUrl())
            .tags("hashTag1,hashTag3")
            .lastChatSenderId(chat4.getUserId())
            .lastChatId(chat4.getId())
            .lastChatMessage(chat4.getMessage())
            .lastChatDispatchTime(chat4.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));

    }

    @Test
    void 강퇴당한_채팅방_조회_성공() throws Exception {
        User user1 = User.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        User user2 = User.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        User user3 = User.builder()
            .nickname("nickname3")
            .profileImageUrl("profileImageUrl3")
            .defaultProfileImageType(2)
            .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Book book = Book.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book)
            .host(user2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        ChatRoomBlockedUser chatRoomBlockedUser = ChatRoomBlockedUser.builder()
            .chatRoom(chatRoom1)
            .user(user3)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUser);

        HashTag tag1 = HashTag.of("hashTag1");
        HashTag tag2 = HashTag.of("hashTag2");
        HashTag tag3 = HashTag.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag3));

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user2).chatRoom(chatRoom2)
            .participantStatus(HOST).build();
        Participant participant3 = Participant.builder().user(user2).chatRoom(chatRoom1)
            .participantStatus(GUEST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);

        Chat chat1 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        Chat chat2 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user2)
            .build();
        Chat chat3 = Chat.builder()
            .chatRoom(chatRoom2)
            .user(user2)
            .build();
        Chat chat4 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .message("test chat5")
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag1"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(user3.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .roomName(chatRoom1.getRoomName())
            .roomSid(chatRoom1.getRoomSid())
            .roomImageUri(chatRoom1.getRoomImageUri())
            .roomMemberCount(2L)
            .roomSize(chatRoom1.getRoomSize())
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .bookTitle(book.getTitle())
            .bookCoverImageUri(book.getBookCoverImageUrl())
            .bookAuthors(book.getAuthors())
            .hostId(user1.getId())
            .hostName(user1.getNickname())
            .hostDefaultProfileImageType(user1.getDefaultProfileImageType())
            .hostProfileImageUri(user1.getProfileImageUrl())
            .tags("hashTag1,hashTag3")
            .lastChatSenderId(chat4.getUserId())
            .lastChatId(chat4.getId())
            .lastChatMessage(chat4.getMessage())
            .lastChatDispatchTime(chat4.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));
    }

    @Test
    void 폭파된_채팅방은_제외되어_조회_성공() throws Exception {
        User user1 = User.builder()
            .nickname("nickname1")
            .profileImageUrl("profileImageUrl1")
            .defaultProfileImageType(1)
            .build();
        User user2 = User.builder()
            .nickname("nickname2")
            .profileImageUrl("profileImageUrl2")
            .defaultProfileImageType(2)
            .build();
        User user3 = User.builder()
            .nickname("nickname3")
            .profileImageUrl("profileImageUrl3")
            .defaultProfileImageType(2)
            .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Book book = Book.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
            .authors(List.of("author1", "author2"))
            .bookCoverImageUrl("bookCoverImage")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomName("chatRoom1")
            .roomSid("chatRoom1")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book)
            .host(user2)
            .roomName("chatRoom2")
            .roomSid("chatRoom2")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();

        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        ChatRoomBlockedUser chatRoomBlockedUser = ChatRoomBlockedUser.builder()
            .chatRoom(chatRoom2)
            .user(user3)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUser);

        HashTag tag1 = HashTag.of("hashTag1");
        HashTag tag2 = HashTag.of("hashTag2");
        HashTag tag3 = HashTag.of("hashTag3");
        hashTagRepository.save(tag1);
        hashTagRepository.save(tag2);
        hashTagRepository.save(tag3);

        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag1));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom2, tag2));
        chatRoomHashTagRepository.save(ChatRoomHashTag.of(chatRoom1, tag3));

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user2).chatRoom(chatRoom2)
            .participantStatus(HOST).build();
        Participant participant3 = Participant.builder().user(user2).chatRoom(chatRoom1)
            .participantStatus(GUEST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);

        Chat chat1 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        Chat chat2 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user2)
            .build();
        Chat chat3 = Chat.builder()
            .chatRoom(chatRoom2)
            .user(user2)
            .build();
        Chat chat4 = Chat.builder()
            .chatRoom(chatRoom1)
            .user(user1)
            .message("test chat5")
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(500L)
            .tags(List.of("hashTag2"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(user3.getId(), chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoom2.getId())
            .roomName(chatRoom2.getRoomName())
            .roomSid(chatRoom2.getRoomSid())
            .roomImageUri(chatRoom2.getRoomImageUri())
            .roomMemberCount(1L)
            .roomSize(chatRoom2.getRoomSize())
            .defaultRoomImageType(chatRoom2.getDefaultRoomImageType())
            .bookTitle(book.getTitle())
            .bookCoverImageUri(book.getBookCoverImageUrl())
            .bookAuthors(book.getAuthors())
            .hostId(user2.getId())
            .hostName(user2.getNickname())
            .hostDefaultProfileImageType(user2.getDefaultProfileImageType())
            .hostProfileImageUri(user2.getProfileImageUrl())
            .tags("hashTag2")
            .lastChatSenderId(chat3.getUserId())
            .lastChatId(chat3.getId())
            .lastChatMessage(chat3.getMessage())
            .lastChatDispatchTime(chat3.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));
    }

    @Test
    void 채팅방_세부정보_조회_성공() throws Exception {
        User aUser = User.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        User bUser = User.builder()
            .nickname("BUser")
            .profileImageUrl("testB@s3.com")
            .defaultProfileImageType(1)
            .build();
        User cUser = User.builder()
            .nickname("CUser")
            .profileImageUrl("testC@s3.com")
            .defaultProfileImageType(1)
            .build();
        userRepository.saveAll(List.of(aUser, bUser, cUser));

        Book book = Book.builder()
            .title("effectiveJava")
            .isbn("tXaid")
            .publishAt(LocalDate.now())
            .authors(List.of("joshua", "jcr"))
            .bookCoverImageUrl("effective@s3.com")
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom = ChatRoom.builder()
            .host(aUser)
            .book(book)
            .roomSid("cES1Cn4N")
            .roomSize(275)
            .defaultRoomImageType(2)
            .build();
        chatRoomRepository.save(chatRoom);

        ChatRoomBlockedUser chatRoomBlockedUser = ChatRoomBlockedUser.builder()
            .user(cUser)
            .chatRoom(chatRoom)
            .build();
        chatRoomBlockedUserRepository.save(chatRoomBlockedUser);

        HashTag tag = HashTag.of("tag1");
        hashTagRepository.save(tag);

        ChatRoomHashTag chatRoomHashTag = ChatRoomHashTag.of(chatRoom, tag);
        chatRoomHashTagRepository.save(chatRoomHashTag);

        Participant participant1 = Participant.builder()
            .user(aUser)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .user(bUser)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant3 = Participant.builder()
            .user(cUser)
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();
        participantRepository.saveAll(List.of(participant1, participant2, participant3));

        ChatRoomDetails real = chatRoomRepository.findChatRoomDetails(chatRoom.getId(), cUser.getId());

        ChatRoomDetails expect = ChatRoomDetails.from(List.of(participant1, participant2, participant3), List.of(tag.getTagName()));

        assertThat(real).isEqualTo(expect);
    }

    @Test
    void 존재하지않는_채팅방_세부정보_조회시_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            chatRoomRepository.findChatRoomDetails(53L, 606L);
        }).isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void 사용자가_접속한_채팅방_조회_성공() throws Exception {
        User user = User.builder().build();
        userRepository.save(user);

        ChatRoom chatRoom = ChatRoom.builder()
            .roomSid("4SyVX")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoom);

        Participant participant = Participant.builder()
            .user(user)
            .chatRoom(chatRoom)
            .participantStatus(GUEST)
            .build();
        participantRepository.save(participant);

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findUserChatRoom(chatRoom.getId(), user.getId());

        assertThat(optionalChatRoom).isPresent();
    }
}