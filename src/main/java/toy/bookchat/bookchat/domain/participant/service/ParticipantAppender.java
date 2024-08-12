package toy.bookchat.bookchat.domain.participant.service;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.GUEST;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.participant.ParticipantEntity;
import toy.bookchat.bookchat.db_module.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.ParticipantStatus;

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

  public void append(Long userId, Long chatRoomId, ParticipantStatus status) {
    ParticipantEntity participantEntity = ParticipantEntity.builder()
        .userId(userId)
        .chatRoomId(chatRoomId)
        .participantStatus(status)
        .build();

    participantRepository.save(participantEntity);

  }
}
