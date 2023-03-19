package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
import toy.bookchat.bookchat.domain.chatroom.ChatRoomHashTag;
import toy.bookchat.bookchat.domain.chatroom.HashTag;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.UserChatRoomResponse;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.ChatRoomRequest;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

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
    ParticipantRepository participantRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    JPAQueryFactory queryFactory;

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
    void 사용자의_채팅방_커서_기반_페이징_조회_커서가없는경우_최신채팅순_최근채팅방순_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);

        Book book = Book.builder()
            .isbn("12342")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomSid("KlV8")
            .roomSize(576)
            .defaultRoomImageType(1)
            .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomSid("IwZrRxR5")
            .roomSize(110)
            .defaultRoomImageType(2)
            .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
            .book(book)
            .host(user2)
            .roomSid("Gmw9yDI4")
            .roomSize(591)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomRepository.save(chatRoom3);

        Chat chat1 = Chat.builder().user(user1).message("a")
            .chatRoom(chatRoom1)
            .build();
        Chat chat2 = Chat.builder().user(user1).message("b")
            .chatRoom(chatRoom2)
            .build();
        Chat chat3 = Chat.builder().user(user1).message("c")
            .chatRoom(chatRoom3)
            .build();
        Chat chat4 = Chat.builder().user(user2).message("d")
            .chatRoom(chatRoom3)
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

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

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoom3.getId())
            .defaultRoomImageType(chatRoom3.getDefaultRoomImageType())
            .roomSid(chatRoom3.getRoomSid())
            .roomMemberCount(2L)
            .lastChatId(chat4.getId())
            .lastChatContent(chat4.getMessage())
            .lastActiveTime(chat4.getCreatedAt())
            .build();

        UserChatRoomResponse userChatRoomResponse2 = UserChatRoomResponse.builder()
            .roomId(chatRoom2.getId())
            .defaultRoomImageType(chatRoom2.getDefaultRoomImageType())
            .roomSid(chatRoom2.getRoomSid())
            .roomMemberCount(1L)
            .lastChatId(chat2.getId())
            .lastChatContent(chat2.getMessage())
            .lastActiveTime(chat2.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        List<UserChatRoomResponse> contents = List.of(userChatRoomResponse1, userChatRoomResponse2);
        Slice<UserChatRoomResponse> result = toSlice(contents, pageRequest);
        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(
            pageRequest, Optional.empty(), user1.getId());
        assertThat(slice.getContent()).usingRecursiveComparison()
            .ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(result.getContent());
    }

    @Test
    void 사용자_채팅방_커서_기반_페이징_조회_커서가_있는경우_최신채팅순_최근채팅방순_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);

        Book book = Book.builder()
            .isbn("12329763345")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomSid("4SyVX")
            .roomSize(77)
            .defaultRoomImageType(1)
            .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomSid("1Y2j9RlN")
            .roomSize(573)
            .defaultRoomImageType(2)
            .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
            .book(book)
            .host(user2)
            .roomSid("r7xr")
            .roomSize(38)
            .defaultRoomImageType(3)
            .build();
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomRepository.save(chatRoom3);

        Chat chat1 = Chat.builder().user(user1).message("a")
            .chatRoom(chatRoom1)
            .build();
        Chat chat2 = Chat.builder().user(user1).message("b")
            .chatRoom(chatRoom2)
            .build();
        Chat chat3 = Chat.builder().user(user2).message("c")
            .chatRoom(chatRoom3)
            .build();
        Chat chat4 = Chat.builder().user(user1).message("d")
            .chatRoom(chatRoom3)
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1)
            .participantStatus(HOST).build();
        Participant participant2 = Participant.builder().user(user1).chatRoom(chatRoom2)
            .participantStatus(GUEST).build();
        Participant participant3 = Participant.builder().user(user1).chatRoom(chatRoom3)
            .participantStatus(SUBHOST).build();
        Participant participant4 = Participant.builder().user(user2).chatRoom(chatRoom3)
            .participantStatus(GUEST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);
        participantRepository.save(participant4);

        UserChatRoomResponse userChatRoomResponse1 = UserChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .roomSid(chatRoom1.getRoomSid())
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .roomMemberCount(1L)
            .lastChatId(chat1.getId())
            .lastChatContent(chat1.getMessage())
            .lastActiveTime(chat1.getCreatedAt())
            .build();

        UserChatRoomResponse userChatRoomResponse2 = UserChatRoomResponse.builder()
            .roomId(chatRoom2.getId())
            .roomSid(chatRoom2.getRoomSid())
            .defaultRoomImageType(chatRoom2.getDefaultRoomImageType())
            .roomMemberCount(1L)
            .lastChatId(chat2.getId())
            .lastChatContent(chat2.getMessage())
            .lastActiveTime(chat2.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<UserChatRoomResponse> result = toSlice(List.of(userChatRoomResponse2,
                userChatRoomResponse1),
            pageRequest);
        Slice<UserChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(
            pageRequest, Optional.of(chat4.getId()), user1.getId());

        assertThat(slice.getContent()).usingRecursiveComparison()
            .ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(result.getContent());
    }

    @Test
    void 채팅방_조회_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        User user3 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Book book = Book.builder()
            .title("가나다 라마 바사")
            .isbn("773898468")
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
            .build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);
        chatRepository.save(chat5);

        PageRequest pageable = PageRequest.of(0, 1);
        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .postCursorId(Optional.of(chatRoom2.getId()))
            .title(Optional.empty())
            .isbn(Optional.empty())
            .roomName(Optional.empty())
            .tags(List.of("hashTag1"))
            .build();

        Slice<ChatRoomResponse> result = chatRoomRepository.findChatRooms(
            chatRoomRequest, pageable);

        ChatRoomResponse expect = ChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .roomName(chatRoom1.getRoomName())
            .roomSid(chatRoom1.getRoomSid())
            .roomImageUri(chatRoom1.getRoomImageUri())
            .roomMemberCount(2L)
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .tags("hashTag1,hashTag3")
            .lastChatId(chat5.getId())
            .lastActiveTime(chat5.getCreatedAt())
            .build();

        assertThat(result.getContent()).isEqualTo(List.of(expect));
    }
}