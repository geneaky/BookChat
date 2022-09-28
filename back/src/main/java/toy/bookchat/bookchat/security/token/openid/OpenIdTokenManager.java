package toy.bookchat.bookchat.security.token.openid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.TokenManager;

@Slf4j
@Component
public class OpenIdTokenManager implements TokenManager {

    private final OpenIdTokenConfig openIdTokenConfig;

    public OpenIdTokenManager(OpenIdTokenConfig openIdTokenConfig) {
        this.openIdTokenConfig = openIdTokenConfig;
    }

    @Override
    public String getOAuth2MemberNumberFromToken(String token, OAuth2Provider oAuth2Provider) {
        OpenIdToken openIdToken = OpenIdToken.of(token);
        return openIdToken.getOAuth2MemberNumber(
            openIdTokenConfig.getPublicKey(openIdToken.getKeyId(), oAuth2Provider));
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        OpenIdToken openIdToken = OpenIdToken.of(token);
        return openIdToken.getEmail(
            openIdTokenConfig.getPublicKey(openIdToken.getKeyId(), oAuth2Provider));
    }
}
