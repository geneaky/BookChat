package toy.bookchat.bookchat.domain.chatroom.api.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class RoomSubHost {

  private final Long id;
  private final String nickname;
  private final String profileImageUrl;
  private final Integer defaultProfileImageType;

  @Builder
  private RoomSubHost(Long id, String nickname, String profileImageUrl,
      Integer defaultProfileImageType) {
    this.id = id;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.defaultProfileImageType = defaultProfileImageType;
  }
}
