package toy.bookchat.bookchat.domain.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.BookEntity;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.chat.ChatEntity;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.ParticipantEntity;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.UserEntity;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

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
            .bookEntity(bookEntity)
            .host(userEntity1)
            .roomSize(348)
            .roomSid("XKewmLwG")
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoomEntity);

        ChatEntity chatEntity0 = ChatEntity.builder().message("enter")
            .chatRoomEntity(chatRoomEntity)
            .build();
        ChatEntity chatEntity1 = ChatEntity.builder().userEntity(userEntity1).message("a")
            .chatRoomEntity(chatRoomEntity)
            .build();
        ChatEntity chatEntity2 = ChatEntity.builder().userEntity(userEntity1).message("b")
            .chatRoomEntity(chatRoomEntity)
            .build();
        ChatEntity chatEntity3 = ChatEntity.builder().userEntity(userEntity2).message("c")
            .chatRoomEntity(chatRoomEntity)
            .build();
        ChatEntity chatEntity4 = ChatEntity.builder().userEntity(userEntity1).message("d")
            .chatRoomEntity(chatRoomEntity)
            .build();

        chatRepository.save(chatEntity0);
        chatRepository.save(chatEntity1);
        chatRepository.save(chatEntity2);
        chatRepository.save(chatEntity3);
        chatRepository.save(chatEntity4);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder()
            .userEntity(userEntity1)
            .chatRoomEntity(chatRoomEntity)
            .participantStatus(HOST)
            .build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder()
            .userEntity(userEntity2)
            .chatRoomEntity(chatRoomEntity)
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
            .host(userEntity1)
            .roomSize(348)
            .roomSid("XKewmLwG")
            .defaultRoomImageType(1)
            .build();
        ChatRoomEntity chatRoomEntity2 = ChatRoomEntity.builder()
            .host(userEntity1)
            .roomSize(200)
            .roomSid("pzSzDwI0Ev")
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.saveAll(List.of(chatRoomEntity1, chatRoomEntity2));

        ChatEntity chatEntity = ChatEntity.builder()
            .userEntity(userEntity1)
            .message("test")
            .chatRoomEntity(chatRoomEntity1)
            .userEntity(userEntity1)
            .build();
        chatRepository.save(chatEntity);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder()
            .userEntity(userEntity1)
            .chatRoomEntity(chatRoomEntity1)
            .participantStatus(HOST)
            .build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder()
            .userEntity(userEntity2)
            .chatRoomEntity(chatRoomEntity1)
            .participantStatus(GUEST)
            .build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder()
            .userEntity(userEntity1)
            .chatRoomEntity(chatRoomEntity2)
            .participantStatus(HOST)
            .build();
        ParticipantEntity participantEntity4 = ParticipantEntity.builder()
            .userEntity(userEntity2)
            .chatRoomEntity(chatRoomEntity2)
            .participantStatus(GUEST)
            .build();
        participantRepository.saveAll(List.of(participantEntity1, participantEntity2, participantEntity3, participantEntity4));

        em.flush();
        em.clear();

        Optional<ChatEntity> optionalChat = chatRepository.getUserChatRoomChat(chatEntity.getId(), userEntity2.getId());
        ChatEntity findChatEntity = optionalChat.get();

        assertAll(
            () -> assertThat(findChatEntity.getUserEntity()).extracting(UserEntity::getId, UserEntity::getNickname, UserEntity::getProfileImageUrl, UserEntity::getDefaultProfileImageType)
                .containsExactly(userEntity1.getId(), userEntity1.getNickname(), userEntity1.getProfileImageUrl(), userEntity1.getDefaultProfileImageType()),
            () -> assertThat(findChatEntity.getChatRoomEntity().getId()).isEqualTo(chatRoomEntity1.getId())
        );
    }
}