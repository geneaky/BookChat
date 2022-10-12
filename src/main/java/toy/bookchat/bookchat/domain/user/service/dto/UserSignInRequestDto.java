package toy.bookchat.bookchat.domain.user.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignInRequestDto {

    private OAuth2Provider oAuth2Provider;
}
