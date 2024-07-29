package toy.bookchat.bookchat.domain.user.api.v1.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {

  private final String accessToken;
  private final String refreshToken;
}
