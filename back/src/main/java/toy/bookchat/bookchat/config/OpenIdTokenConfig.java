package toy.bookchat.bookchat.config;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import toy.bookchat.bookchat.security.exception.NotVerifiedRequestFormatException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.keys.GooglePublicKeys;
import toy.bookchat.bookchat.security.token.openid.keys.KakaoPublicKeys;

@Component
public class OpenIdTokenConfig {

    public static final String RSA = "RSA";
    //설정 정보는 설정 파일로 빼야함 (상수와 설정을 구분해서 사용)
    public static final String KAKAO_PUBLIC_KEY_REQUEST_URI = "https://kauth.kakao.com/.well-known/jwks.json";
    private final RestTemplate restTemplate;
    private final ConcurrentHashMap<OAuth2Provider, LocalDateTime> publicKeysCachedTime;
    private final KeyFactory keyFactory;
    private KakaoPublicKeys kakaoPublicKeys;
    private GooglePublicKeys googlePublicKeys;

    public OpenIdTokenConfig(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.publicKeysCachedTime = new ConcurrentHashMap<>();
        this.keyFactory = createKeyFactory();
    }

    public Key getPublicKey(String keyId, OAuth2Provider oAuth2Provider) {

        if (OAuth2Provider.KAKAO.equals(oAuth2Provider)) {
            checkKakaoPublicKeyCache();
            return this.kakaoPublicKeys.getKey(keyId, this.keyFactory);
        }

        if (OAuth2Provider.GOOGLE.equals(oAuth2Provider)) {
            checkGooglePublicKeyCache();
            return this.googlePublicKeys.getKey(keyId, this.keyFactory);
        }

        throw new NotVerifiedRequestFormatException(keyId);
    }

    private KeyFactory createKeyFactory() {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException ignore) {
        }
        return keyFactory;
    }

    private void checkGooglePublicKeyCache() {
        if (isGooglePublicKeyCacheExpired()) {
            refreshGooglePublicKeys();
        }
    }

    private boolean isGooglePublicKeyCacheExpired() {
        return LocalDateTime.now()
                .isAfter(publicKeysCachedTime.getOrDefault(OAuth2Provider.GOOGLE, LocalDateTime.MIN));
    }

    private void refreshGooglePublicKeys() {
        this.googlePublicKeys = fetchGooglePublicKey();
        this.publicKeysCachedTime.put(OAuth2Provider.GOOGLE, LocalDateTime.now().plusDays(3L));
    }

    private GooglePublicKeys fetchGooglePublicKey() {
        return null;
    }

    private void checkKakaoPublicKeyCache() {
        if (isKakaoPublicKeyCacheExpired()) {
            refreshKakaoPublicKeys();
        }
    }

    private boolean isKakaoPublicKeyCacheExpired() {
        return LocalDateTime.now()
                .isAfter(publicKeysCachedTime.getOrDefault(OAuth2Provider.KAKAO, LocalDateTime.MIN));
    }

    private void refreshKakaoPublicKeys() {
        this.kakaoPublicKeys = fetchKakaoPublicKey();
        this.publicKeysCachedTime.put(OAuth2Provider.KAKAO, LocalDateTime.now().plusDays(3L));
    }

    private KakaoPublicKeys fetchKakaoPublicKey() {
        return restTemplate.exchange(KAKAO_PUBLIC_KEY_REQUEST_URI,
            HttpMethod.GET,
            null, KakaoPublicKeys.class).getBody();
    }
}
