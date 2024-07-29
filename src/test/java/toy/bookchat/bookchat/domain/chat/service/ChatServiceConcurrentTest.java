package toy.bookchat.bookchat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static toy.bookchat.bookchat.domain.common.Status.ACTIVE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.infrastructure.broker.MessagePublisher;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@SpringBootTest
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
  private ChatRepository chatRepository;
  @MockBean
  private MessagePublisher messagePublisher;

  @Test
  @DisplayName("제한된 인원수 채팅방 입장 동시성 테스트 성공")
  void enterChatRoom() throws Exception {
    int roomSize = 5;
    int count = 10;

    UserEntity host = UserEntity.builder()
        .nickname("host")
        .defaultProfileImageType(1)
        .provider(OAuth2Provider.GOOGLE)
        .email("host")
        .name("host")
        .status(ACTIVE)
        .build();
    userRepository.save(host);

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
        .hostId(host.getId())
        .bookId(bookEntity.getId())
        .defaultRoomImageType(1)
        .roomSize(roomSize)
        .roomSid("HNsIG51b")
        .roomName("RtzE")
        .build();
    chatRoomRepository.save(chatRoomEntity);

    CountDownLatch countDownLatch = new CountDownLatch(count);
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    AtomicInteger result = new AtomicInteger(0);
    for (UserEntity userEntity : userEntityList) {
      executorService.execute(() -> {
        try {
          chatRoomService.enterChatRoom(userEntity.getId(), chatRoomEntity.getId());
          result.incrementAndGet();
        } catch (Exception ignore) {
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    countDownLatch.await();

    assertThat(result.get()).isEqualTo(roomSize);

    userRepository.deleteAll();
    bookRepository.deleteAllInBatch();
    chatRoomRepository.deleteAllInBatch();
    chatRepository.deleteAllInBatch();
    participantRepository.deleteAllInBatch();
  }

}
