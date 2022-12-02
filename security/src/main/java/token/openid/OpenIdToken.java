package token.openid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.exception.security.IllegalStandardTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenIdToken {

    public static final int STANDARD_TOKEN_LENGTH = 3;
    public static final int HEADER = 0;
    public static final int PAYLOAD = 1;
    public static final String KID = "kid";
    public static final String EMAIL = "email";
    private final String token;

    public static OpenIdToken of(String token) {
        return new OpenIdToken(token);
    }

    public String getOAuth2MemberNumber(Key publicKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSubject(publicKey));

        String issuer = getIssuer(publicKey);
        if (issuer.contains(OAuth2Provider.KAKAO.getValue().toLowerCase())) {
            stringBuilder.append(OAuth2Provider.KAKAO.getValue());
            return stringBuilder.toString();
        }

        if (issuer.contains(OAuth2Provider.GOOGLE.getValue().toLowerCase())) {
            stringBuilder.append(OAuth2Provider.GOOGLE.getValue());
            return stringBuilder.toString();
        }

        throw new DenidedTokenException();
    }

    public String getEmail(Key publicKey) {
        return (String) Optional.ofNullable(getBody(publicKey).get(EMAIL))
            .orElseThrow(IllegalStandardTokenException::new);
    }

    private Claims getBody(Key publicKey) {
        try {
            return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(this.token)
                .getBody();

        } catch (ExpiredJwtException exception) {
            log.info("Token Is Expired :: {}", this.token);
            throw new ExpiredTokenException(exception.getMessage());
        } catch (JwtException | IllegalStateException exception) {
            log.info("Token Is Denied :: {}", this.token);
            throw new DenidedTokenException(exception.getMessage());
        }
    }

    private String getIssuer(Key publicKey) {
        return Optional.ofNullable(getBody(publicKey).getIssuer())
            .orElseThrow(IllegalStandardTokenException::new);
    }

    private String getSubject(Key publicKey) {
        return Optional.ofNullable(getBody(publicKey).getSubject())
            .orElseThrow(IllegalStandardTokenException::new);
    }

    public String getKeyId() {
        return (String) Optional.ofNullable(getHeader()
            .get(KID)).orElseThrow(IllegalStandardTokenException::new);
    }

    private Header getHeader() {
        validateTokenLength();
        try {
            return Jwts.parser()
                .parse(getUnsignedTokenBuilder(this.token))
                .getHeader();
        } catch (ExpiredJwtException exception) {
            log.info("Token Is Expired :: {}", this.token);
            throw new ExpiredTokenException(exception.getMessage());
        }
    }

    private void validateTokenLength() {
        if (this.token.split("\\.").length != STANDARD_TOKEN_LENGTH) {
            throw new IllegalStandardTokenException();
        }
    }

    private String getUnsignedTokenBuilder(String openIdToken) {
        String[] tokenParts = divideTokenIntoParts(openIdToken);

        StringBuilder unsignedTokenBuilder = new StringBuilder();
        unsignedTokenBuilder.append(tokenParts[HEADER]);
        unsignedTokenBuilder.append(".");
        unsignedTokenBuilder.append(tokenParts[PAYLOAD]);
        unsignedTokenBuilder.append(".");

        return unsignedTokenBuilder.toString();
    }

    private String[] divideTokenIntoParts(String openIdToken) {
        return openIdToken.split("\\.");
    }
}
