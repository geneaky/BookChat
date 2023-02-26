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
    void 채팅방_참여인원_조회_성공() throws Exception {
        User aUser = User.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        User bUser = User.builder()
            .nickname("BUser")
            .profileImageUrl("testB@s3.com")
            .defaultProfileImageType(1)
            .build();
        User cUser = User.builder()
            .nickname("CUser")
            .profileImageUrl("testC@s3.com")
            .defaultProfileImageType(1)
            .build();
        userRepository.saveAll(List.of(aUser, bUser, cUser));

        ChatRoom chatRoom = ChatRoom.builder()
            .host(aUser)
            .build();
        chatRoomRepository.save(chatRoom);

        Participant participant1 = Participant.builder()
            .user(aUser)
            .participantStatus(HOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .user(bUser)
            .participantStatus(SUBHOST)
            .chatRoom(chatRoom)
            .build();
        Participant participant3 = Participant.builder()
            .user(cUser)
            .participantStatus(GUEST)
            .chatRoom(chatRoom)
            .build();
        participantRepository.saveAll(List.of(participant1, participant2, participant3));

        List<Participant> chatRoomUsers = participantRepository.findChatRoomUsers(chatRoom.getId(),
            cUser.getId());

        assertThat(chatRoomUsers).containsExactly(participant1, participant2, participant3);
    }

    @Test
    void 사용자Id와_채팅방Id로_참여자_조회_성공() throws Exception {
        User user = User.builder()
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();

        userRepository.save(user);

        ChatRoom chatRoom = ChatRoom.builder()
            .host(user)
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
}