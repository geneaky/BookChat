package toy.bookchat.bookchat.security.openid;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenIdTokenManager {

    private final JwtTokenConfig jwtTokenConfig;

    public String getOauth2MemberNumberFromRequest(String openIdToken) {
        String oauth2MemberNumber;
        try {
            oauth2MemberNumber = Jwts.parser()
                .setSigningKey(jwtTokenConfig.getSecret())
                .parseClaimsJws(openIdToken)
                .getBody()
                .getSubject();
        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", openIdToken);
            throw new ExpiredTokenException(exception.getMessage(), exception);
        } catch (JwtException exception) {
            log.info("Token :: {} :: is denied", openIdToken);
            throw new DenidedTokenException(exception.getMessage(), exception);
        }
        return oauth2MemberNumber;
    }
}
