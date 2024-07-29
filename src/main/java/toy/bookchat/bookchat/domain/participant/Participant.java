package toy.bookchat.bookchat.domain.participant;

import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.HOST;
import static toy.bookchat.bookchat.domain.participant.ParticipantStatus.SUBHOST;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Participant {

  private final Long id;
  private final Long userId;
  private final Long chatRoomId;
  private ParticipantStatus status;

  @Builder
  private Participant(Long id, Long userId, Long chatRoomId, ParticipantStatus status) {
    this.id = id;
    this.userId = userId;
    this.chatRoomId = chatRoomId;
    this.status = status;
  }

  public boolean isSubHost() {
    return status == SUBHOST;
  }

  public boolean isNotHost() {
    return status != HOST;
  }

  public void changeStatus(ParticipantStatus participantStatus) {
    this.status = participantStatus;
  }
}
