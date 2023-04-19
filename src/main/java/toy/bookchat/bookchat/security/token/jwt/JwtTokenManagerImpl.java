package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.exception.security.DeniedTokenException;
import toy.bookchat.bookchat.security.user.TokenPayload;

@Component
public class JwtTokenManagerImpl implements JwtTokenManager {

    private final String BEARER = "Bearer ";
    private final int BEGIN_INDEX = 7;
    private final JwtTokenProperties jwtTokenProperties;

    public JwtTokenManagerImpl(JwtTokenProperties jwtTokenProperties) {
        this.jwtTokenProperties = jwtTokenProperties;
    }

    @Override
    public String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER)) {
            return authorizationHeader.substring(BEGIN_INDEX);
        }

        throw new DeniedTokenException("Token is Empty");
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
