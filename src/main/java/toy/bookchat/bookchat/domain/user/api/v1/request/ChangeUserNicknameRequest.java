package toy.bookchat.bookchat.domain.user.api.v1.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeUserNicknameRequest {

  @NotBlank
  private String nickname;
  @NotNull
  private Boolean isProfileChanged;

  public ChangeUserNicknameRequest(String nickname, Boolean isProfileChanged) {
    this.nickname = nickname;
    this.isProfileChanged = isProfileChanged;
  }

  public boolean doesChangeProfileImage() {
    return this.isProfileChanged;
  }
}
