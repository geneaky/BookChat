package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Component
public class JwtTokenManagerImpl implements JwtTokenManager {

    private final JwtTokenConfig jwtTokenConfig;

    public JwtTokenManagerImpl(JwtTokenConfig jwtTokenConfig) {
        this.jwtTokenConfig = jwtTokenConfig;
    }

    @Override
    public String getOAuth2MemberNumberFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getOAuth2MemberNumber(jwtTokenConfig.getSecret());
    }

    @Override
    public String getUserEmailFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getEmail(jwtTokenConfig.getSecret());
    }

    @Override
    public OAuth2Provider getOAuth2ProviderFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getOAuth2Provider(jwtTokenConfig.getSecret());
    }

    @Override
    public boolean shouldRefreshTokenBeRenewed(String token) {
        return false;
    }
}
