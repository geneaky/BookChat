package toy.bookchat.bookchat.security.token.openid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import toy.bookchat.bookchat.exception.security.NotSupportedOAuth2ProviderException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoIdToken {

    public static final String EMAIL = "email";
    private final String token;

    public static KakaoIdToken of(String token) {
        return new KakaoIdToken(token);
    }

    public String getOAuth2MemberNumber(Key publicKey, String kakaoAppKey) {
        if (isKakaoIssuer(publicKey, kakaoAppKey)) {
            return getSubject(publicKey, kakaoAppKey) + OAuth2Provider.KAKAO.getValue();
        }

        throw new NotSupportedOAuth2ProviderException();
    }

    public String getEmail(Key publicKey, String kakaoAppKey) {
        return (String) Optional.ofNullable(getBody(publicKey, kakaoAppKey).get(EMAIL))
            .orElseThrow(IllegalStandardTokenException::new);
    }

    private boolean isKakaoIssuer(Key publicKey, String kakaoAppKey) {
        return getIssuer(publicKey, kakaoAppKey).contains(
            OAuth2Provider.KAKAO.getValue().toLowerCase());
    }

    private Claims getBody(Key publicKey, String kakaoAppKey) {
        try {
            Claims body = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(this.token)
                .getBody();
            validateTokenAudience(body, kakaoAppKey);
            return body;
        } catch (ExpiredJwtException exception) {
            log.info("Token Is Expired :: {}", this.token);
            throw new ExpiredTokenException(exception.getMessage());
        } catch (JwtException | IllegalStateException exception) {
            log.info("Token Is Denied :: {}", this.token);
            throw new DenidedTokenException(exception.getMessage());
        }
    }

    private static void validateTokenAudience(Claims body, String kakaoAppKey) {
        String aud = body.getAudience();
        if (!kakaoAppKey.equals(aud)) {
            throw new DenidedTokenException();
        }
    }

    private String getIssuer(Key publicKey, String kakaoAppKey) {
        return Optional.ofNullable(getBody(publicKey, kakaoAppKey).getIssuer())
            .orElseThrow(IllegalStandardTokenException::new);
    }

    private String getSubject(Key publicKey, String kakaoAppKey) {
        return Optional.ofNullable(getBody(publicKey, kakaoAppKey).getSubject())
            .orElseThrow(IllegalStandardTokenException::new);
    }
}
