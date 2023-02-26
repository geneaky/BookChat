package toy.bookchat.bookchat.security.token.openid.kakao;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import toy.bookchat.bookchat.exception.security.DeniedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.exception.security.IllegalStandardTokenException;

@Slf4j
@Component
public class KakaoPublicKeyFetcher {

    public static final String RSA = "RSA";
    public static final int STANDARD_TOKEN_LENGTH = 3;
    public static final int HEADER = 0;
    public static final int PAYLOAD = 1;
    public static final String KID = "kid";
    private final WebClient webClient;
    private final KeyFactory keyFactory;

    public KakaoPublicKeyFetcher(WebClient webClient) {
        this.webClient = webClient;
        this.keyFactory = createKeyFactory();
    }

    public Key getPublicKey(String token, String kakaoUri) {
        return fetchKakaoPublicKey(kakaoUri).getKey(getKeyId(token),
            this.keyFactory);
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
        return webClient.get()
            .uri(kakaoUri)
            .retrieve()
            .bodyToMono(KakaoPublicKeys.class).block();
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
        } catch (Exception exception) {
            throw new DeniedTokenException();
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
