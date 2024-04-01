package toy.bookchat.bookchat.domain.participant.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@RepositoryTest
class ParticipantRepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void 사용자Id와_채팅방Id로_참여자_조회_성공() throws Exception {
        User user = User.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();

        userRepository.save(user);

        ChatRoom chatRoom = ChatRoom.builder()
            .host(user)
            .roomSid("KUor")
            .roomSize(655)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoom);

        Participant participant = Participant.builder()
            .user(user)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();

        participantRepository.save(participant);

        Participant findParticipant = participantRepository.findByUserIdAndChatRoomId(user.getId(),
            chatRoom.getId()).get();

        assertThat(findParticipant).isEqualTo(participant);
    }

    @Test
    void 채팅방_부방장_인원수_조회_성공() throws Exception {
        User user1 = User.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        User user2 = User.builder()
            .nickname("BUser")
            .defaultProfileImageType(2)
            .build();

        userRepository.save(user1);
        userRepository.save(user2);

        ChatRoom chatRoom = ChatRoom.builder()
            .host(user1)
            .roomSid("KUor")
            .roomSize(655)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoom);

        Participant participant1 = Participant.builder()
            .user(user1)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();

        Participant participant2 = Participant.builder()
            .user(user2)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();

        participantRepository.save(participant1);
        participantRepository.save(participant2);

        Long memberOfSubHost = participantRepository.countSubHostByRoomId(chatRoom.getId());

        assertThat(memberOfSubHost).isEqualTo(1);
    }

    @Test
    void 채팅방에_접속한_참여자수_조회_성공() throws Exception {
        ChatRoom chatRoom = ChatRoom.builder()
            .roomSid("KUor")
            .roomSize(655)
            .defaultRoomImageType(1)
            .build();
        chatRoomRepository.save(chatRoom);

        Participant participant1 = Participant.builder()
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant3 = Participant.builder()
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();
        participantRepository.saveAll(List.of(participant1, participant2, participant3));

        Long memberCount = participantRepository.countByChatRoom(chatRoom);

        assertThat(memberCount).isEqualTo(3);
    }
}