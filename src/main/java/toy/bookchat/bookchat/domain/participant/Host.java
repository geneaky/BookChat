package toy.bookchat.bookchat.domain.participant;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Host {

  private final Long id;
  private final Long userId;
  private final Long chatRoomId;
  private ParticipantStatus status;

  @Builder
  private Host(Long id, Long userId, Long chatRoomId, ParticipantStatus status) {
    this.id = id;
    this.userId = userId;
    this.chatRoomId = chatRoomId;
    this.status = status;
  }

  public void changeStatus(ParticipantStatus participantStatus) {
    this.status = participantStatus;
  }
}
