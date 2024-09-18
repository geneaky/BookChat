package toy.bookchat.bookchat.domain.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

  private final Long id;
  private final String nickname;
  private final String email;
  private final String profileImageUrl;
  private final Integer defaultProfileImageType;

  @Builder
  public User(Long id, String nickname, String email, String profileImageUrl, Integer defaultProfileImageType) {
    this.id = id;
    this.nickname = nickname;
    this.email = email;
    this.profileImageUrl = profileImageUrl;
    this.defaultProfileImageType = defaultProfileImageType;
  }
}
