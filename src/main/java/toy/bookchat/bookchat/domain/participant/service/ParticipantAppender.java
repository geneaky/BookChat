package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.Participant;

@Component
public class ParticipantAppender {

  private final ParticipantRepository participantRepository;

  public ParticipantAppender(ParticipantRepository participantRepository) {
    this.participantRepository = participantRepository;
  }

  public void append(Participant participant) {
    ParticipantEntity participantEntity = ParticipantEntity.builder()
        .participantStatus(GUEST)
        .userId(participant.getUserId())
        .chatRoomId(participant.getChatRoomId())
        .build();

    participantRepository.save(participantEntity);
  }
}
