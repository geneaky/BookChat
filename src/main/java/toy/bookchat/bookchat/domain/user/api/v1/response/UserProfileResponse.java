package toy.bookchat.bookchat.domain.user.api.v1.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.User;

@Getter
@Builder
public class UserProfileResponse {

  private Long userId;
  private String userNickname;
  private String userEmail;
  private String userProfileImageUri;
  private Integer defaultProfileImageType;

  public static UserProfileResponse of(User user) {
    return UserProfileResponse.builder()
        .userId(user.getId())
        .userNickname(user.getNickname())
        .userEmail(user.getEmail())
        .userProfileImageUri(user.getProfileImageUrl())
        .defaultProfileImageType(user.getDefaultProfileImageType())
        .build();
  }
}
