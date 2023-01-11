package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;

@Component
public class JwtTokenManagerImpl implements JwtTokenManager {

    private final JwtTokenProperties jwtTokenProperties;

    public JwtTokenManagerImpl(JwtTokenProperties jwtTokenProperties) {
        this.jwtTokenProperties = jwtTokenProperties;
    }

    @Override
    public Long getUserIdFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getUserId(jwtTokenProperties.getSecret());
    }

    @Override
    public String getOAuth2MemberNumberFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getOAuth2MemberNumber(jwtTokenProperties.getSecret());
    }

    @Override
    public String getUserEmailFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getEmail(jwtTokenProperties.getSecret());
    }

    @Override
    public OAuth2Provider getOAuth2ProviderFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getOAuth2Provider(jwtTokenProperties.getSecret());
    }

    @Override
    public boolean shouldRefreshTokenBeRenew(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.hasNotRemainingTime(jwtTokenProperties.getSecret(),
            jwtTokenProperties.getReissuePeriod());
    }

    @Override
    public TokenPayload getTokenPayloadFromToken(String token) {
        JwtToken jwtToken = JwtToken.of(token);
        return jwtToken.getPayload(jwtTokenProperties.getSecret());
    }
}
