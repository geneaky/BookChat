package toy.bookchat.bookchat.security.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.exception.IllegalStandardTokenException;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtToken {

    public static final String EMAIL = "email";
    private String jwtToken;

    public static JwtToken of(String jwtToken) {
        return new JwtToken(jwtToken);
    }

    public String getOAuth2MemberNumber(String secret) {
        return (String) Optional.ofNullable(getBody(secret).get("userName"))
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
                .parseClaimsJws(this.jwtToken)
                .getBody()).orElseThrow(() -> {
                throw new IllegalStandardTokenException("Token body is not existed");
            });
        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", this.jwtToken);
            throw new ExpiredTokenException(exception.getMessage(), exception);
        } catch (IllegalArgumentException | JwtException exception) {
            log.info("Token :: {} :: is denied", this.jwtToken);
            throw new DenidedTokenException(exception.getMessage(), exception);
        }
    }

}