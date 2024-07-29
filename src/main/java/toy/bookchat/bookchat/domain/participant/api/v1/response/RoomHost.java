package toy.bookchat.bookchat.domain.participant.api.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class RoomHost {

  private final Long id;
  private final String nickname;
  private final String profileImageUrl;
  private final Integer defaultProfileImageType;

  @Builder
  private RoomHost(Long id, String nickname, String profileImageUrl,
      Integer defaultProfileImageType) {
    this.id = id;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.defaultProfileImageType = defaultProfileImageType;
  }
}
