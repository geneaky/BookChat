package toy.bookchat.bookchat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class ChatServiceConcurrentTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private ChatService chatService;
    @Autowired
    private Flyway flyway;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeAll
    public void tearUp() {
        flyway.clean();
        flyway.migrate();
    }

    @AfterAll
    public void tearDown() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void 제한된_인원수_채팅방_입장_동시성_테스트_성공() throws Exception {
        int roomSize = 20;
        int count = 100;
        CountDownLatch countDownLatch = new CountDownLatch(count);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = User.builder()
                .nickname(i + "nickname")
                .defaultProfileImageType(1)
                .provider(OAuth2Provider.KAKAO)
                .email(i + "email")
                .name(i + "name")
                .build();

            userList.add(user);
        }
        userRepository.saveAll(userList);

        Book book = Book.builder()
            .isbn("4640485366")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(book);

        ChatRoom chatRoom = ChatRoom.builder()
            .host(userList.get(0))
            .book(book)
            .defaultRoomImageType(1)
            .roomSize(roomSize)
            .roomSid("HNsIG51b")
            .roomName("RtzE")
            .build();
        chatRoomRepository.save(chatRoom);

        for (User user : userList) {
            executorService.execute(() -> {
                try {
                    log.info(user.getId().toString());
                    chatService.enterChatRoom(user.getId(), chatRoom.getId());
                } catch (Exception exception) {
                    log.info(exception.getMessage());
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        List<Participant> participants = participantRepository.findAll();
        assertThat(participants.size()).isEqualTo(roomSize);
    }

}
