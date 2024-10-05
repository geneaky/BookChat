package toy.bookchat.bookchat.domain.chat;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Getter
public class Sender {

  private Long id;
  private String nickname;
  private String profileImageUrl;
  private Integer defaultProfileImageType;

  @Builder
  private Sender(Long id, String nickname, String profileImageUrl, Integer defaultProfileImageType) {
    this.id = id;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.defaultProfileImageType = defaultProfileImageType;
  }

  public static Sender from(User user) {
    if (user == null) {
      return null;
    }
    return new Sender(user.getId(), user.getNickname(), user.getProfileImageUrl(), user.getDefaultProfileImageType());
  }
}
