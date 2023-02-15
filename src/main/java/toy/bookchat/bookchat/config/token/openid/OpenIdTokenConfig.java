package toy.bookchat.bookchat.config.token.openid;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.exception.security.NotVerifiedRequestFormatException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.keys.GooglePublicKeys;
import toy.bookchat.bookchat.security.token.openid.keys.KakaoPublicKeys;

@Component
public class OpenIdTokenConfig {

    public static final String RSA = "RSA";
    public static final String KAKAO_APP_KEY = "bde877e4a4632685835cd00431e5dc2d";
    private final OAuth2Properties oAuth2Properties;
    private final RestTemplate restTemplate;
    private final KeyFactory keyFactory;
    
    public OpenIdTokenConfig(RestTemplateBuilder restTemplateBuilder,
        OAuth2Properties oAuth2Properties) {
        this.restTemplate = restTemplateBuilder.build();
        this.oAuth2Properties = oAuth2Properties;
        this.keyFactory = createKeyFactory();
    }

    /* TODO: 2022-09-23 app key로 검증하는거까지 추가하자(구글 openid 검증방식 확인 후)
     */

    public Key getPublicKey(String keyId, OAuth2Provider oAuth2Provider) {

        if (OAuth2Provider.KAKAO.equals(oAuth2Provider)) {
            return fetchKakaoPublicKey().getKey(keyId, this.keyFactory);
        }

        if (OAuth2Provider.GOOGLE.equals(oAuth2Provider)) {
            return fetchGooglePublicKey().getKey(keyId, this.keyFactory);
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

    @Cacheable(cacheNames = "google", key = "#root.methodName")
    public GooglePublicKeys fetchGooglePublicKey() {
        return null;
    }

    @Cacheable(cacheNames = "kakao", key = "#root.methodName")
    public KakaoPublicKeys fetchKakaoPublicKey() {
        return restTemplate.exchange(oAuth2Properties.getKakaoUri(),
            HttpMethod.GET,
            null, KakaoPublicKeys.class).getBody();
    }
}
