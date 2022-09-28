package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.TokenManager;

@Component
@Qualifier("jwtTokenManager")
public class JwtTokenManager implements TokenManager {

    private final JwtTokenConfig jwtTokenConfig;

    public JwtTokenManager(JwtTokenConfig jwtTokenConfig) {
        this.jwtTokenConfig = jwtTokenConfig;
    }

    @Override
    public String getOAuth2MemberNumberFromToken(String token, OAuth2Provider oAuth2Provider) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getOAuth2MemberNumber(jwtTokenConfig.getSecret());
    }

    @Override
    public String getUserEmailFromToken(String token, OAuth2Provider oAuth2Provider) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getEmail(jwtTokenConfig.getSecret());
    }
}
