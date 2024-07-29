package toy.bookchat.bookchat.domain.participant;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipantAdmin {

  private final Long userId;
  private final ParticipantStatus status;

  @Builder
  private ParticipantAdmin(Long userId, ParticipantStatus status) {
    this.userId = userId;
    this.status = status;
  }

  public boolean isSubHost() {
    return status == ParticipantStatus.SUBHOST;
  }

  public boolean isHost() {
    return status == ParticipantStatus.HOST;
  }
}
