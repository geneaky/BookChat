package toy.bookchat.bookchat.domain.user.api.v1.response;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.user.UserProfile;

@Getter
@Builder
public class UserProfileResponse {

  private Long userId;
  private String userNickname;
  private String userEmail;
  private String userProfileImageUri;
  private Integer defaultProfileImageType;

  public static UserProfileResponse of(UserProfile userProfile) {
    return UserProfileResponse.builder()
        .userId(userProfile.getUserId())
        .userNickname(userProfile.getUserNickname())
        .userEmail(userProfile.getUserEmail())
        .userProfileImageUri(userProfile.getUserProfileImageUri())
        .defaultProfileImageType(userProfile.getDefaultProfileImageType())
        .build();
  }
}
