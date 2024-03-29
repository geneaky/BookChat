package toy.bookchat.bookchat.domain.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chat.Chat;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
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
    void 채팅_내역_조회_성공() throws Exception {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        userRepository.save(user1);
        userRepository.save(user2);

        Book book = Book.builder()
            .isbn("12345")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom = ChatRoom.builder()
            .book(book)
            .host(user1)
            .roomSize(348)
            .roomSid("XKewmLwG")
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoom);

        Chat chat0 = Chat.builder().message("enter")
            .chatRoom(chatRoom)
            .build();
        Chat chat1 = Chat.builder().user(user1).message("a")
            .chatRoom(chatRoom)
            .build();
        Chat chat2 = Chat.builder().user(user1).message("b")
            .chatRoom(chatRoom)
            .build();
        Chat chat3 = Chat.builder().user(user2).message("c")
            .chatRoom(chatRoom)
            .build();
        Chat chat4 = Chat.builder().user(user1).message("d")
            .chatRoom(chatRoom)
            .build();

        chatRepository.save(chat0);
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        chatRepository.save(chat4);

        Participant participant1 = Participant.builder().user(user1).chatRoom(chatRoom)
            .participantStatus(
                ParticipantStatus.HOST).build();
        Participant participant2 = Participant.builder().user(user2).chatRoom(chatRoom)
            .participantStatus(ParticipantStatus.GUEST).build();
        participantRepository.save(participant1);
        participantRepository.save(participant2);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());

        List<Chat> content = chatRepository.getChatRoomChats(chatRoom.getId(), chat3.getId(),
            pageRequest, user1.getId()).getContent();

        assertThat(content).containsExactly(chat2, chat1, chat0);
    }
}