package toy.bookchat.bookchat.security.token.jwt;

import static toy.bookchat.bookchat.security.token.TokenConstants.DEFAULT_PROFILE_IMAGE_TYPE;
import static toy.bookchat.bookchat.security.token.TokenConstants.EMAIL;
import static toy.bookchat.bookchat.security.token.TokenConstants.PROVIDER;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_ID;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_NAME;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_NICKNAME;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_PROFILE_IMAGE_URI;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_ROLE;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.exception.security.IllegalStandardTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtToken {

    private String token;

    public static JwtToken of(String jwtToken) {
        return new JwtToken(jwtToken);
    }

    public Long getUserId(String secret) {
        return Long.valueOf((String) Optional.ofNullable(getBody(secret).get(USER_ID))
            .orElseThrow(() -> {
                throw new IllegalStandardTokenException("User id is not existed");
            }));
    }

    public String getOAuth2MemberNumber(String secret) {
        return (String) Optional.ofNullable(getBody(secret).get(USER_NAME))
            .orElseThrow(() -> {
                throw new IllegalStandardTokenException("User name is not existed");
            });
    }

    public String getEmail(String secret) {
        return (String) Optional.ofNullable(getBody(secret).get(EMAIL))
            .orElseThrow(() -> {
                throw new IllegalStandardTokenException("Email is not existed");
            });
    }

    private Claims getBody(String secret) {
        try {
            return Optional.ofNullable(Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(this.token)
                .getBody()).orElseThrow(() -> {
                throw new IllegalStandardTokenException("Token body is not existed");
            });
        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", this.token);
            throw new ExpiredTokenException(exception.getMessage(), exception);
        } catch (IllegalArgumentException | JwtException exception) {
            log.info("Token :: {} :: is denied", this.token);
            throw new DenidedTokenException(exception.getMessage(), exception);
        }
    }

    public OAuth2Provider getOAuth2Provider(String secret) {
        return OAuth2Provider.from((String) Optional.ofNullable(getBody(secret).get(PROVIDER))
            .orElseThrow(() -> {
                throw new IllegalStandardTokenException("Provider is not existed");
            }));
    }

    public boolean hasNotRemainingTime(String secret, long reissuePeriod) {
        Date now = new Date();
        Date expiration = getBody(secret).getExpiration();
        return now.after(new Date(expiration.getTime() - reissuePeriod));
    }

    public TokenPayload getPayload(String secret) {
        Claims body = getBody(secret);
        return TokenPayload.of(
            Long.valueOf((String) body.get(USER_ID)),
            (String) body.get(USER_NAME),
            (String) body.get(USER_NICKNAME),
            (String) body.get(EMAIL),
            (String) body.get(USER_PROFILE_IMAGE_URI),
            (Integer) body.get(DEFAULT_PROFILE_IMAGE_TYPE),
            ROLE.value((String) body.get(USER_ROLE)));
    }
}
