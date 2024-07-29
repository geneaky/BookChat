package toy.bookchat.bookchat.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@ExtendWith(MockitoExtension.class)
class ParticipantManagerTest {

    @Mock
    private ParticipantRepository participantRepository;
    @InjectMocks
    private ParticipantManager participantManager;


    @Test
    void 채팅방에_참여한_사용자라면_접속상태로_변경_성공() throws Exception {
        ParticipantEntity participantEntity = ParticipantEntity.builder().isConnected(false).build();
        given(participantRepository.findByUserIdAndChatRoomSid(any(), any())).willReturn(Optional.of(participantEntity));

        participantManager.connect(1L, "KUor");

        assertThat(participantEntity.getIsConnected()).isTrue();
    }

    @Test
    void 채팅방연결_시도시_참여하지않은_사용자라면_예외발생_성공() throws Exception {
        assertThatThrownBy(() -> participantManager.connect(1L, "KUor")).isInstanceOf(ParticipantNotFoundException.class);
    }
}