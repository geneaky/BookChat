package toy.bookchat.bookchat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static toy.bookchat.bookchat.domain.common.Status.ACTIVE;

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
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.infrastructure.push.service.PushService;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ChatServiceConcurrentTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private ChatRoomService chatRoomService;
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
    void 제한된_인원수_채팅방_입장_동시성_테스트_성공() throws Exception {
        int roomSize = 20;
        int count = 100;
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
                .status(ACTIVE)
                .build());
        }
        userRepository.saveAll(userEntityList);

        BookEntity bookEntity = BookEntity.builder()
            .isbn("4640485366")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .host(userEntityList.get(0))
            .bookId(bookEntity.getId())
            .defaultRoomImageType(1)
            .roomSize(roomSize)
            .roomSid("HNsIG51b")
            .roomName("RtzE")
            .build();
        chatRoomRepository.save(chatRoomEntity);

        for (UserEntity userEntity : userEntityList) {
            executorService.execute(() -> {
                try {
                    log.info(userEntity.getId().toString());
                    chatRoomService.enterChatRoom(userEntity.getId(), chatRoomEntity.getId());
                } catch (Exception exception) {
                    log.info(exception.getMessage());
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        List<ParticipantEntity> participantEntities = participantRepository.findAll();
        assertThat(participantEntities.size()).isEqualTo(roomSize);
    }

}
