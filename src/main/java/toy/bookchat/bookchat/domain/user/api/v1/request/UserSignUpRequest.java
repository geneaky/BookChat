package toy.bookchat.bookchat.domain.user.api.v1.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpRequest {

  @NotBlank
  private String nickname;
  @JsonProperty("readingTastes")
  @NotNull
  private List<ReadingTaste> readingTastes;
  @NotNull
  private Integer defaultProfileImageType;
  @NotNull
  @JsonProperty("oauth2Provider")
  private OAuth2Provider oauth2Provider;

  @Builder
  private UserSignUpRequest(String nickname, List<ReadingTaste> readingTastes,
      Integer defaultProfileImageType, OAuth2Provider oauth2Provider) {
    this.nickname = nickname;
    this.readingTastes = readingTastes;
    this.defaultProfileImageType = defaultProfileImageType;
    this.oauth2Provider = oauth2Provider;
  }

  public UserEntity getUser(String oauth2MemberNumber, String email, String profileImageUrl) {
    return UserEntity.builder()
        .name(oauth2MemberNumber)
        .nickname(this.nickname)
        .email(email)
        .profileImageUrl(profileImageUrl)
        .role(ROLE.USER)
        .provider(this.oauth2Provider)
        .readingTastes(this.readingTastes)
        .defaultProfileImageType(this.defaultProfileImageType)
        .build();
  }
}
