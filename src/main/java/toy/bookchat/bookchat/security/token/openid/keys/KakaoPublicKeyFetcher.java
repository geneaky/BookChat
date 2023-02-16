package toy.bookchat.bookchat.security.token.openid.keys;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.exception.security.IllegalStandardTokenException;
import toy.bookchat.bookchat.exception.security.NotSupportedOAuth2ProviderException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@Component
public class KakaoPublicKeyFetcher {

    public static final String RSA = "RSA";
    public static final int STANDARD_TOKEN_LENGTH = 3;
    public static final int HEADER = 0;
    public static final int PAYLOAD = 1;
    public static final String KID = "kid";
    private final RestTemplate restTemplate;
    private final KeyFactory keyFactory;

    public KakaoPublicKeyFetcher(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.keyFactory = createKeyFactory();
    }

    public Key getPublicKey(String token, OAuth2Provider oAuth2Provider, String kakaoUri) {
        if (OAuth2Provider.KAKAO.equals(oAuth2Provider)) {
            return fetchKakaoPublicKey(kakaoUri).getKey(getKeyId(token), this.keyFactory);
        }

        throw new NotSupportedOAuth2ProviderException();
    }

    private KeyFactory createKeyFactory() {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException ignore) {
        }
        return keyFactory;
    }

    @Cacheable(cacheNames = "kakao", key = "#root.methodName")
    public KakaoPublicKeys fetchKakaoPublicKey(String kakaoUri) {
        return restTemplate.exchange(kakaoUri, HttpMethod.GET, null, KakaoPublicKeys.class)
            .getBody();
    }

    private String getKeyId(String token) {
        return (String) Optional.ofNullable(getHeader(token)
            .get(KID)).orElseThrow(IllegalStandardTokenException::new);
    }

    private Header getHeader(String token) {
        validateTokenLength(token);
        try {
            return Jwts.parser()
                .parse(getUnsignedTokenBuilder(token))
                .getHeader();
        } catch (ExpiredJwtException exception) {
            log.info("Token Is Expired :: {}", token);
            throw new ExpiredTokenException(exception.getMessage());
        }
    }

    private void validateTokenLength(String token) {
        if (token.split("\\.").length != STANDARD_TOKEN_LENGTH) {
            throw new IllegalStandardTokenException();
        }
    }

    private String getUnsignedTokenBuilder(String token) {
        String[] tokenParts = divideTokenIntoParts(token);

        return tokenParts[HEADER] + "." + tokenParts[PAYLOAD] + ".";
    }

    private String[] divideTokenIntoParts(String token) {
        return token.split("\\.");
    }
}
