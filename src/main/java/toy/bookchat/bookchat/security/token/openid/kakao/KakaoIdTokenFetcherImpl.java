package toy.bookchat.bookchat.security.token.openid.kakao;

import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.exception.security.DeniedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.exception.security.IllegalStandardTokenException;
import toy.bookchat.bookchat.exception.security.NotSupportedOAuth2ProviderException;
import toy.bookchat.bookchat.security.token.openid.KakaoIdToken;

@Slf4j
@Component
public class KakaoIdTokenFetcherImpl implements KakaoIdTokenFetcher {

    public static final String EMAIL = "email";
    private final OAuth2Properties oAuth2Properties;
    private final KakaoPublicKeyFetcher kakaoPublicKeyFetcher;

    public KakaoIdTokenFetcherImpl(OAuth2Properties oAuth2Properties,
        KakaoPublicKeyFetcher kakaoPublicKeyFetcher) {
        this.oAuth2Properties = oAuth2Properties;
        this.kakaoPublicKeyFetcher = kakaoPublicKeyFetcher;
    }

    private static boolean hasNotSameIssuer(Claims body) {
        if (body.getIssuer() != null) {
            return !body.getIssuer().contains(KAKAO.getValue().toLowerCase());
        }

        return true;
    }

    private static boolean hasNotEmail(Claims body) {
        if (body.get(EMAIL) != null) {
            return !StringUtils.hasText((String) body.get(EMAIL));
        }

        return true;
    }

    private static boolean hasNotSubject(Claims body) {
        if (body.getSubject() != null) {
            return body.getSubject().isBlank();
        }
        return true;
    }

    private boolean hasNotSameAppKey(Claims body) {
        if (body.getAudience() != null) {
            return !body.getAudience().equals(oAuth2Properties.getKakaoAppKey());
        }

        return true;
    }

    @Override
    public KakaoIdToken fetchKakaoIdToken(String idToken) {
        Claims body = getBody(idToken,
            kakaoPublicKeyFetcher.getPublicKey(idToken, oAuth2Properties.getKakaoUri()));
        return KakaoIdToken.from(body.getSubject(), (String) body.get(EMAIL));
    }

    private Claims getBody(String idToken, Key publicKey) {
        try {
            Claims body = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(idToken)
                .getBody();
            validateKakaoToken(body);
            return body;
        } catch (ExpiredJwtException exception) {
            log.info("Token Is Expired :: {}", idToken);
            throw new ExpiredTokenException(exception.getMessage());
        } catch (JwtException | IllegalStateException exception) {
            log.info("Token Is Denied :: {}", idToken);
            throw new DeniedTokenException(exception.getMessage());
        }
    }

    private void validateKakaoToken(Claims body) {
        if (hasNotSubject(body)) {
            throw new IllegalStandardTokenException();
        }

        if (hasNotEmail(body)) {
            throw new IllegalStandardTokenException();
        }

        if (hasNotSameAppKey(body)) {
            throw new DeniedTokenException();
        }

        if (hasNotSameIssuer(body)) {
            throw new NotSupportedOAuth2ProviderException();
        }
    }
}
