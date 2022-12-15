package toy.bookchat.bookchat.security.token.openid;

import static toy.bookchat.bookchat.domain.common.AuthConstants.BEGIN_INDEX;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.security.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@Component
public class OpenIdTokenManagerImpl implements OpenIdTokenManager {

    private final OpenIdTokenConfig openIdTokenConfig;

    public OpenIdTokenManagerImpl(OpenIdTokenConfig openIdTokenConfig) {
        this.openIdTokenConfig = openIdTokenConfig;
    }

    @Override
    public String getOAuth2MemberNumberFromToken(String token, OAuth2Provider oAuth2Provider) {
        OpenIdToken openIdToken = OpenIdToken.of(getOpenIdToken(token));
        return openIdToken.getOAuth2MemberNumber(
            openIdTokenConfig.getPublicKey(openIdToken.getKeyId(), oAuth2Provider));
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        OpenIdToken openIdToken = OpenIdToken.of(getOpenIdToken(token));
        return openIdToken.getEmail(
            openIdTokenConfig.getPublicKey(openIdToken.getKeyId(), oAuth2Provider));
    }

    private String getOpenIdToken(String bearerToken) {
        return bearerToken.substring(BEGIN_INDEX);
    }
}
