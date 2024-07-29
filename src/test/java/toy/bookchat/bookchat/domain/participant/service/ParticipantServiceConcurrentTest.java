package toy.bookchat.bookchat.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.firebase.messaging.FirebaseMessaging;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

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
    @MockBean
    private PushService pushService;
    @MockBean
    private FirebaseMessaging firebaseMessaging;

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

        List<UserEntity> userEntityList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userEntityList.add(UserEntity.builder()
                .nickname(i + "nickname")
                .defaultProfileImageType(1)
                .provider(OAuth2Provider.KAKAO)
                .email(i + "email")
                .name(i + "name")
                .build());
        }
        userRepository.saveAll(userEntityList);

        BookEntity bookEntity = BookEntity.builder()
            .isbn("4640485366")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .hostId(userEntityList.get(0).getId())
            .bookId(bookEntity.getId())
            .defaultRoomImageType(1)
            .roomSize(200)
            .roomSid("HNsIG51b")
            .roomName("RtzE")
            .build();
        chatRoomRepository.save(chatRoomEntity);

        List<ParticipantEntity> participantEntityList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                participantEntityList.add(ParticipantEntity.builder()
                    .userId(userEntityList.get(i).getId())
                    .chatRoomId(chatRoomEntity.getId())
                    .participantStatus(ParticipantStatus.HOST)
                    .build());
                continue;
            }
            participantEntityList.add(ParticipantEntity.builder()
                .userId(userEntityList.get(i).getId())
                .chatRoomId(chatRoomEntity.getId())
                .participantStatus(ParticipantStatus.GUEST)
                .build());
        }
        participantRepository.saveAll(participantEntityList);

        countDownLatch.countDown();
        for (int i = 1; i < count; i++) {
            final int idx = i;
            executorService.execute(() -> {
                try {
                    participantService.changeParticipantRights(chatRoomEntity.getId(),
                        participantEntityList.get(idx).getUserId(), ParticipantStatus.HOST,
                        participantEntityList.get(0).getUserId());
                } catch (Exception exception) {
                    log.info(exception.getMessage());
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        long hostCount = participantEntityList.stream()
            .filter(p -> p.getParticipantStatus() == ParticipantStatus.HOST).count();

        assertThat(hostCount).isOne();
    }
}