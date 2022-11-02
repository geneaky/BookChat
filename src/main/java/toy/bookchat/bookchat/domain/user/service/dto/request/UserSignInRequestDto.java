package toy.bookchat.bookchat.domain.user.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignInRequestDto {

    @NotNull
    @JsonProperty("oauth2Provider")
    private OAuth2Provider oauth2Provider;

    @Builder
    public UserSignInRequestDto(OAuth2Provider oauth2Provider) {
        this.oauth2Provider = oauth2Provider;
    }
}