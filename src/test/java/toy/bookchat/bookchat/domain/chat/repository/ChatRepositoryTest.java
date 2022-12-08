package toy.bookchat.bookchat.domain.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@RepositoryTest
class ChatRepositoryTest {

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
    void 사용자의_채팅방_커서_기반_페이징_조회_커서가없는경우_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);

        Book book = Book.builder().build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder().book(book).build();
        ChatRoom chatRoom2 = ChatRoom.builder().book(book).build();
        ChatRoom chatRoom3 = ChatRoom.builder().book(book).build();
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomRepository.save(chatRoom3);

        Chat chat1 = Chat.builder().user(user1).message("a").chatRoom(chatRoom1).build();
        Chat chat2 = Chat.builder().user(user1).message("b").chatRoom(chatRoom2).build();
        Chat chat3 = Chat.builder().user(user1).message("c").chatRoom(chatRoom3).build();
        Chat chat4 = Chat.builder().user(user2).message("d").chatRoom(chatRoom3).build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1).build();
        Participant participant2 = Participant.builder().user(user1).chatRoom(chatRoom2).build();
        Participant participant3 = Participant.builder().user(user1).chatRoom(chatRoom3).build();
        Participant participant4 = Participant.builder().user(user2).chatRoom(chatRoom3).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);
        participantRepository.save(participant4);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Slice<Chat> slice = chatRepository.findUserChatRoomsWithLastChat(
            Optional.empty(), pageRequest, user1.getId());
        assertThat(slice.getContent()).containsExactly(chat4, chat2);
    }

    @Test
    void 사용자_채팅방_커서_기반_페이징_조회_커서가_있는경우_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);

        Book book = Book.builder().build();
        bookRepository.save(book);

        ChatRoom chatRoom1 = ChatRoom.builder().book(book).build();
        ChatRoom chatRoom2 = ChatRoom.builder().book(book).build();
        ChatRoom chatRoom3 = ChatRoom.builder().book(book).build();
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
        chatRoomRepository.save(chatRoom3);

        Chat chat1 = Chat.builder().user(user1).message("a").chatRoom(chatRoom1).build();
        Chat chat2 = Chat.builder().user(user1).message("b").chatRoom(chatRoom2).build();
        Chat chat3 = Chat.builder().user(user1).message("c").chatRoom(chatRoom3).build();
        Chat chat4 = Chat.builder().user(user2).message("d").chatRoom(chatRoom3).build();
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom1).build();
        Participant participant2 = Participant.builder().user(user1).chatRoom(chatRoom2).build();
        Participant participant3 = Participant.builder().user(user1).chatRoom(chatRoom3).build();
        Participant participant4 = Participant.builder().user(user2).chatRoom(chatRoom3).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);
        participantRepository.save(participant4);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").ascending());
        Slice<Chat> slice = chatRepository.findUserChatRoomsWithLastChat(
            Optional.of(2L), pageRequest, user1.getId());

        assertThat(slice.getContent()).containsExactly(chat4);
    }
}