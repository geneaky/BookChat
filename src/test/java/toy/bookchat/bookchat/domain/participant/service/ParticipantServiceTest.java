package toy.bookchat.bookchat.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.service.dto.ChatRoomParticipantsResponse;
import toy.bookchat.bookchat.domain.user.User;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;
    @InjectMocks
    private ParticipantService participantService;

    @Test
    void 채팅방_참여인원_조회_성공() throws Exception {
        User aUser = User.builder()
            .id(1L)
            .nickname("AUser")
            .defaultProfileImageType(1)
            .build();
        User bUser = User.builder()
            .id(2L)
            .nickname("BUser")
            .profileImageUrl("testB@s3.com")
            .defaultProfileImageType(1)
            .build();
        User cUser = User.builder()
            .id(2L)
            .nickname("CUser")
            .profileImageUrl("testC@s3.com")
            .defaultProfileImageType(1)
            .build();
        ChatRoom chatRoom = ChatRoom.builder()
            .host(aUser)
            .build();

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

        List<Participant> participantList = List.of(participant1, participant2, participant3);
        ChatRoomParticipantsResponse expect = ChatRoomParticipantsResponse.from(participantList);
        when(participantRepository.findChatRoomUsers(any(), any())).thenReturn(participantList);
        ChatRoomParticipantsResponse result = participantService.getChatRoomUsers(1L, 1L);

        assertThat(result).isEqualTo(expect);
        verify(participantRepository).findChatRoomUsers(any(), any());
    }
}