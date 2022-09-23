package toy.bookchat.bookchat.security.token.openid;

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
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.exception.IllegalStandardTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenIdToken {

    public static final int STANDARD_TOKEN_LENGTH = 3;
    public static final int HEADER = 0;
    public static final int PAYLOAD = 1;
    public static final String KID = "kid";
    private final String openidToken;

    public static OpenIdToken of(String openidToken) {
        return new OpenIdToken(openidToken);
    }

    public String getOAuth2MemberNumber(Key publicKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSubject(publicKey));

        String issuer = getIssuer(publicKey);
        if (issuer.contains(OAuth2Provider.KAKAO.getValue())) {
            stringBuilder.append(OAuth2Provider.KAKAO.getValue());
            return stringBuilder.toString();
        }

        if (issuer.contains(OAuth2Provider.GOOGLE.getValue())) {
            stringBuilder.append(OAuth2Provider.GOOGLE.getValue());
            return stringBuilder.toString();
        }

        throw new DenidedTokenException("Not Allowed Format Token Exception");
    }

    public String getEmail(Key publicKey) {
        return (String) Optional.ofNullable(getBody(publicKey).get("email")).orElseThrow(() -> {
            throw new IllegalStandardTokenException("Email is not existed");
        });
    }

    private Claims getBody(Key publicKey) {
        try {
            return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(this.openidToken)
                .getBody();
        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", this.openidToken);
            throw new ExpiredTokenException(exception.getMessage(), exception);
        } catch (JwtException exception) {
            log.info("Token :: {} :: is denied", this.openidToken);
            throw new DenidedTokenException(exception.getMessage(), exception);
        }
    }

    private String getIssuer(Key publicKey) {
        return Optional.ofNullable(getBody(publicKey).getIssuer())
            .orElseThrow(() -> {
                throw new IllegalStandardTokenException("Issuer is not existed");
            });
    }

    private String getSubject(Key publicKey) {
        return Optional.ofNullable(getBody(publicKey).getSubject()).orElseThrow(() -> {
            throw new IllegalStandardTokenException("Subject is not existed");
        });
    }

    public String getKeyId() {
        return (String) Optional.ofNullable(getHeader()
            .get(KID)).orElseThrow(() -> {
            throw new IllegalStandardTokenException("KeyId is not existed");
        });
    }

    private Header getHeader() {
        validateTokenLength();
        try {
            return Jwts.parser()
                .parse(getUnsignedTokenBuilder(this.openidToken))
                .getHeader();
        } catch (ExpiredJwtException exception) {
            log.info("Token :: {} :: is expired", this.openidToken);
            throw new ExpiredTokenException(exception.getMessage(), exception);
        } catch (JwtException exception) {
            log.info("Token :: {} :: is denied", this.openidToken);
            throw new DenidedTokenException(exception.getMessage(), exception);
        }
    }

    private void validateTokenLength() {
        if (this.openidToken.split("\\.").length != STANDARD_TOKEN_LENGTH) {
            throw new IllegalStandardTokenException("Illegal Standard Token Length");
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
