package toy.bookchat.bookchat.domain.user.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignInRequestDto {

    @NotNull
    @JsonProperty("oauth2Provider")
    private OAuth2Provider oauth2Provider;
}