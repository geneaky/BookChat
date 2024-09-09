package toy.bookchat.bookchat.domain.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.chat.ChatEntity;
import toy.bookchat.bookchat.db_module.chat.repository.ChatRepository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;

class ChatEntityRepositoryTest extends RepositoryTest {

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
  @Autowired
  EntityManager em;

  @Test
  void 채팅_내역_조회_성공() throws Exception {
    UserEntity userEntity1 = UserEntity.builder().build();
    UserEntity userEntity2 = UserEntity.builder().build();
    userRepository.save(userEntity1);
    userRepository.save(userEntity2);

    BookEntity bookEntity = BookEntity.builder()
        .isbn("12345")
        .publishAt(LocalDate.now())
        .build();
    bookRepository.save(bookEntity);

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .bookId(bookEntity.getId())
        .roomSize(348)
        .roomSid("XKewmLwG")
        .defaultRoomImageType(1)
        .build();
    chatRoomRepository.save(chatRoomEntity);

    ChatEntity chatEntity0 = ChatEntity.builder().message("enter")
        .chatRoomId(chatRoomEntity.getId())
        .build();
    ChatEntity chatEntity1 = ChatEntity.builder().userId(userEntity1.getId()).message("a")
        .chatRoomId(chatRoomEntity.getId())
        .build();
    ChatEntity chatEntity2 = ChatEntity.builder().userId(userEntity1.getId()).message("b")
        .chatRoomId(chatRoomEntity.getId())
        .build();
    ChatEntity chatEntity3 = ChatEntity.builder().userId(userEntity2.getId()).message("c")
        .chatRoomId(chatRoomEntity.getId())
        .build();
    ChatEntity chatEntity4 = ChatEntity.builder().userId(userEntity1.getId()).message("d")
        .chatRoomId(chatRoomEntity.getId())
        .build();

    chatRepository.save(chatEntity0);
    chatRepository.save(chatEntity1);
    chatRepository.save(chatEntity2);
    chatRepository.save(chatEntity3);
    chatRepository.save(chatEntity4);

    ParticipantEntity participantEntity1 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity2 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity.getId())
        .participantStatus(GUEST)
        .build();
    participantRepository.save(participantEntity1);
    participantRepository.save(participantEntity2);

    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());

    List<ChatEntity> content = chatRepository.getChatRoomChats(chatRoomEntity.getId(), chatEntity3.getId(),
        pageRequest, userEntity1.getId()).getContent();

    assertThat(content).containsExactly(chatEntity2, chatEntity1, chatEntity0);
  }

  @Test
  void 사용자_채팅방의_채팅_채팅방_발신자_정보_조회_성공() throws Exception {
    UserEntity userEntity1 = UserEntity.builder()
        .nickname("iNsCmHX")
        .profileImageUrl("f2Rd26gOi")
        .defaultProfileImageType(865)
        .build();
    UserEntity userEntity2 = UserEntity.builder()
        .nickname("fnHl69h")
        .profileImageUrl("F7xaZQaPsF")
        .defaultProfileImageType(706)
        .build();
    userRepository.saveAll(List.of(userEntity1, userEntity2));

    ChatRoomEntity chatRoomEntity1 = ChatRoomEntity.builder()
        .bookId(1L)
        .roomSize(348)
        .roomSid("XKewmLwG")
        .defaultRoomImageType(1)
        .build();
    ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
        .bookId(2L)
        .roomSize(200)
        .roomSid("pzSzDwI0Ev")
        .defaultRoomImageType(1)
        .build();
    chatRoomRepository.saveAll(List.of(chatRoomEntity1, chatRoomEntity2));

    ChatEntity chatEntity = ChatEntity.builder()
        .userId(userEntity1.getId())
        .message("test")
        .chatRoomId(chatRoomEntity1.getId())
        .userId(userEntity1.getId())
        .build();
    chatRepository.save(chatEntity);

    ParticipantEntity participantEntity1 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity2 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity1.getId())
        .participantStatus(GUEST)
        .build();
    ParticipantEntity participantEntity3 = ParticipantEntity.builder()
        .userId(userEntity1.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(HOST)
        .build();
    ParticipantEntity participantEntity4 = ParticipantEntity.builder()
        .userId(userEntity2.getId())
        .chatRoomId(chatRoomEntity2.getId())
        .participantStatus(GUEST)
        .build();
    participantRepository.saveAll(
        List.of(participantEntity1, participantEntity2, participantEntity3, participantEntity4));

    em.flush();
    em.clear();

    Optional<ChatEntity> optionalChat = chatRepository.getUserChatRoomChat(chatEntity.getId(), userEntity2.getId());
    ChatEntity findChatEntity = optionalChat.get();

    assertThat(findChatEntity).extracting(ChatEntity::getUserId, ChatEntity::getChatRoomId)
        .containsExactly(userEntity1.getId(), chatRoomEntity1.getId());
  }
}