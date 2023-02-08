package toy.bookchat.bookchat.domain.participant.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
            .chatRoom(chatRoom)
            .build();
        Participant participant2 = Participant.builder()
            .user(bUser)
            .chatRoom(chatRoom)
            .isSubHost(true)
            .build();
        Participant participant3 = Participant.builder()
            .user(cUser)
            .chatRoom(chatRoom)
            .isSubHost(false)
            .build();
        participantRepository.saveAll(List.of(participant1, participant2, participant3));

        List<Participant> chatRoomUsers = participantRepository.findChatRoomUsers(chatRoom.getId(),
            cUser.getId());

        assertThat(chatRoomUsers).containsExactly(participant1, participant2, participant3);
    }

}