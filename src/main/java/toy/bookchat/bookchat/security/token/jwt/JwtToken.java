package toy.bookchat.bookchat.security.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.exception.unauthorized.DeniedTokenException;
import toy.bookchat.bookchat.exception.unauthorized.ExpiredTokenException;
import toy.bookchat.bookchat.exception.unauthorized.IllegalStandardTokenException;
import toy.bookchat.bookchat.security.user.TokenPayload;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtToken {

    private final String USER_NAME = "userName";
    private final String USER_ID = "userId";
    private final String EMAIL = "email";
    private final String USER_NICKNAME = "userNickname";
    private final String USER_PROFILE_IMAGE_URI = "userProfileImageUri";
    private final String DEFAULT_PROFILE_IMAGE_TYPE = "defaultProfileImageType";
    private final String USER_ROLE = "userRole";

    private String token;

    public static JwtToken of(String jwtToken) {
        return new JwtToken(jwtToken);
    }

    public Long getUserId(String secret) {
        return Long.valueOf((String) Optional.ofNullable(getBody(secret).get(USER_ID))
            .orElseThrow(IllegalStandardTokenException::new));
    }

    public String getOAuth2MemberNumber(String secret) {
        return (String) Optional.ofNullable(getBody(secret).get(USER_NAME))
            .orElseThrow(IllegalStandardTokenException::new);
    }

    public String getEmail(String secret) {
        return (String) Optional.ofNullable(getBody(secret).get(EMAIL))
            .orElseThrow(IllegalStandardTokenException::new);
    }

    private Claims getBody(String secret) {
        try {
            return Optional.ofNullable(Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(this.token)
                .getBody()).orElseThrow(IllegalStandardTokenException::new);
        } catch (ExpiredJwtException exception) {
            throw new ExpiredTokenException();
        } catch (Exception exception) {
            throw new DeniedTokenException();
        }
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
