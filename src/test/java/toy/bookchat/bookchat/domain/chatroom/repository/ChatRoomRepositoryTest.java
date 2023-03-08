package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;
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
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;
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

        ChatRoomResponse chatRoomResponse1 = ChatRoomResponse.builder()
            .roomId(chatRoom3.getId())
            .defaultRoomImageType(chatRoom3.getDefaultRoomImageType())
            .roomSid(chatRoom3.getRoomSid())
            .roomMemberCount(2L)
            .lastChatId(chat4.getId())
            .lastChatContent(chat4.getMessage())
            .lastActiveTime(chat4.getCreatedAt())
            .build();

        ChatRoomResponse chatRoomResponse2 = ChatRoomResponse.builder()
            .roomId(chatRoom2.getId())
            .defaultRoomImageType(chatRoom2.getDefaultRoomImageType())
            .roomSid(chatRoom2.getRoomSid())
            .roomMemberCount(1L)
            .lastChatId(chat2.getId())
            .lastChatContent(chat2.getMessage())
            .lastActiveTime(chat2.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        List<ChatRoomResponse> contents = List.of(chatRoomResponse1, chatRoomResponse2);
        Slice<ChatRoomResponse> result = toSlice(contents, pageRequest);
        Slice<ChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(
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
            .isbn("12345")
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

        ChatRoomResponse chatRoomResponse1 = ChatRoomResponse.builder()
            .roomId(chatRoom1.getId())
            .roomSid(chatRoom1.getRoomSid())
            .defaultRoomImageType(chatRoom1.getDefaultRoomImageType())
            .roomMemberCount(1L)
            .lastChatId(chat1.getId())
            .lastChatContent(chat1.getMessage())
            .lastActiveTime(chat1.getCreatedAt())
            .build();

        ChatRoomResponse chatRoomResponse2 = ChatRoomResponse.builder()
            .roomId(chatRoom2.getId())
            .roomSid(chatRoom2.getRoomSid())
            .defaultRoomImageType(chatRoom2.getDefaultRoomImageType())
            .roomMemberCount(1L)
            .lastChatId(chat2.getId())
            .lastChatContent(chat2.getMessage())
            .lastActiveTime(chat2.getCreatedAt())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<ChatRoomResponse> result = toSlice(List.of(chatRoomResponse2, chatRoomResponse1),
            pageRequest);
        Slice<ChatRoomResponse> slice = chatRoomRepository.findUserChatRoomsWithLastChat(
            pageRequest, Optional.of(chat4.getId()), user1.getId());

        assertThat(slice.getContent()).usingRecursiveComparison()
            .ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(result.getContent());
    }
}