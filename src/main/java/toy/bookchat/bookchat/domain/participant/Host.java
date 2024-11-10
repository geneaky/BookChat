package toy.bookchat.bookchat.domain.participant;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Getter
public class Host {

  private final Long id;
  private final Long userId;
  private final Long chatRoomId;
  private final String nickname;
  private final String profileImageUrl;
  private final Integer defaultProfileImageType;
  private ParticipantStatus status;

  @Builder
  private Host(Long id, Long userId, Long chatRoomId, String nickname, String profileImageUrl,
      Integer defaultProfileImageType, ParticipantStatus status) {
    this.id = id;
    this.userId = userId;
    this.chatRoomId = chatRoomId;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.defaultProfileImageType = defaultProfileImageType;
    this.status = status;
  }

  public void changeStatus(ParticipantStatus participantStatus) {
    this.status = participantStatus;
  }

  public boolean isNotSameUser(User user) {
    return this.userId != user.getId();
  }

  public boolean isSameUser(Long userId) {
    return this.userId == userId;
  }
}
