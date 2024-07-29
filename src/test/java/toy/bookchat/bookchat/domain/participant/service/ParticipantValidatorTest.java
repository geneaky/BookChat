package toy.bookchat.bookchat.domain.participant.service;

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
import toy.bookchat.bookchat.exception.badrequest.participant.AlreadyParticipateException;
import toy.bookchat.bookchat.exception.notfound.pariticipant.ParticipantNotFoundException;

@ExtendWith(MockitoExtension.class)
class ParticipantValidatorTest {

  @Mock
  private ParticipantRepository participantRepository;
  @InjectMocks
  private ParticipantValidator participantValidator;

  @Test
  void 채팅방_참여자가_아닌경우_예외발생_성공() throws Exception {
    assertThatThrownBy(() -> participantValidator.checkDoesUserParticipate(1L, 1L))
        .isInstanceOf(ParticipantNotFoundException.class);
  }

  @Test
  void 이미_채팅방_참여자인_경우_예외발생_성공() throws Exception {
    ParticipantEntity participantEntity = ParticipantEntity.builder().build();
    given(participantRepository.findByUserIdAndChatRoomId(any(), any())).willReturn(Optional.of(participantEntity));

    assertThatThrownBy(() -> participantValidator.checkDoesUserAlreadyParticipate(1L, 1L))
        .isInstanceOf(AlreadyParticipateException.class);
  }
}