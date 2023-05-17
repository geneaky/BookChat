package toy.bookchat.bookchat.domain.participant.service;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParticipantServiceConcurrentTest {

    @Autowired
    private ParticipantService participantService;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private Flyway flyway;
    @MockBean
    private MessagePublisher messagePublisher;

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
    void 게스트_방장_위임_동시성_테스트() throws Exception {
        int count = 50;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userList.add(User.builder()
                    .nickname(i + "nickname")
                    .defaultProfileImageType(1)
                    .provider(OAuth2Provider.KAKAO)
                    .email(i + "email")
                    .name(i + "name")
                    .build());
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
                .roomSize(200)
                .roomSid("HNsIG51b")
                .roomName("RtzE")
                .build();
        chatRoomRepository.save(chatRoom);

        List<Participant> participantList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                participantList.add(Participant.builder()
                        .user(userList.get(i))
                        .chatRoom(chatRoom)
                        .participantStatus(ParticipantStatus.HOST)
                        .build());
                continue;
            }
            participantList.add(Participant.builder()
                    .user(userList.get(i))
                    .chatRoom(chatRoom)
                    .participantStatus(ParticipantStatus.GUEST)
                    .build());
        }
        participantRepository.saveAll(participantList);

        countDownLatch.countDown();
        for (int i = 1; i < count; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    participantService.changeParticipantRights(chatRoom.getId(), participantList.get(idx).getUserId(), ParticipantStatus.HOST, participantList.get(0).getUserId());
                } catch (Exception exception) {
                    log.info(exception.getMessage());
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        long hostCount = participantList.stream().filter(p -> p.getParticipantStatus() == ParticipantStatus.HOST).count();

        assertThat(hostCount).isOne();
    }
}