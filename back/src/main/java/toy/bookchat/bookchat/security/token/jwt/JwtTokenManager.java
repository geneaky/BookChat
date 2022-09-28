package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.TokenManager;

@Component
@Qualifier("jwtTokenManager")
public class JwtTokenManager implements TokenManager {
    @Override
    public String getOAuth2MemberNumberFromToken(String token, OAuth2Provider oAuth2Provider) {
        return null;
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        return null;
    }
}
