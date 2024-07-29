package toy.bookchat.bookchat.domain.participant.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;

class ParticipantEntityRepositoryTest extends RepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void 사용자Id와_채팅방Id로_참여자_조회_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();

        userRepository.save(userEntity);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .bookId(1L)
            .hostId(userEntity.getId())
            .roomSid("KUor")
            .roomSize(655)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoomEntity);

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .userId(userEntity.getId())
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(HOST)
            .build();

        participantRepository.save(participantEntity);

        ParticipantEntity findParticipantEntity = participantRepository.findByUserIdAndChatRoomId(userEntity.getId(),
            chatRoomEntity.getId()).get();

        assertThat(findParticipantEntity).isEqualTo(participantEntity);
    }

    @Test
    void 채팅방_부방장_인원수_조회_성공() throws Exception {
        UserEntity userEntity1 = UserEntity.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        UserEntity userEntity2 = UserEntity.builder()
            .nickname("BUser")
            .defaultProfileImageType(2)
            .build();

        userRepository.save(userEntity1);
        userRepository.save(userEntity2);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .bookId(1L)
            .hostId(userEntity1.getId())
            .roomSid("KUor")
            .roomSize(655)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoomEntity);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder()
            .userId(userEntity1.getId())
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(HOST)
            .build();

        ParticipantEntity participantEntity2 = ParticipantEntity.builder()
            .userId(userEntity2.getId())
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(SUBHOST)
            .build();

        participantRepository.save(participantEntity1);
        participantRepository.save(participantEntity2);

        Long memberOfSubHost = participantRepository.countSubHostByRoomId(chatRoomEntity.getId());

        assertThat(memberOfSubHost).isEqualTo(1);
    }

    @Test
    void 채팅방에_접속한_참여자수_조회_성공() throws Exception {
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .bookId(645L)
            .hostId(384L)
            .roomSid("KUor")
            .roomSize(655)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoomEntity);

        ParticipantEntity participantEntity1 = ParticipantEntity.builder()
            .userId(384L)
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(HOST)
            .build();
        ParticipantEntity participantEntity2 = ParticipantEntity.builder()
            .userId(1L)
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(SUBHOST)
            .build();
        ParticipantEntity participantEntity3 = ParticipantEntity.builder()
            .userId(2L)
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(GUEST)
            .build();
        participantRepository.saveAll(List.of(participantEntity1, participantEntity2, participantEntity3));

        Long memberCount = participantRepository.countByChatRoomId(chatRoomEntity.getId());

        assertThat(memberCount).isEqualTo(3);
    }

    @Test
    void 이미_채팅방에_접속한_사용자는_중복저장_실패() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();

        userRepository.save(userEntity);

        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
            .bookId(1L)
            .hostId(userEntity.getId())
            .roomSid("KUor")
            .roomSize(655)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoomEntity);

        ParticipantEntity participantEntity = ParticipantEntity.builder()
            .userId(userEntity.getId())
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(HOST)
            .build();

        participantRepository.save(participantEntity);

        ParticipantEntity duplicateParticipantEntity = ParticipantEntity.builder()
            .userId(userEntity.getId())
            .chatRoomId(chatRoomEntity.getId())
            .participantStatus(GUEST)
            .build();

        assertThatThrownBy(() -> participantRepository.save(duplicateParticipantEntity))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}