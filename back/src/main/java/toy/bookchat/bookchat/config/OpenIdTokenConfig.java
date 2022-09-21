package toy.bookchat.bookchat.config;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import toy.bookchat.bookchat.security.exception.NotVerifiedRequestFormatException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.keys.GooglePublicKeys;
import toy.bookchat.bookchat.security.token.openid.keys.KakaoPublicKeys;

@Component
public class OpenIdTokenConfig {

    private final RestTemplate restTemplate;
    private KakaoPublicKeys kakaoPublicKeys;
    private GooglePublicKeys googlePublicKeys;
    private ConcurrentHashMap<OAuth2Provider, LocalDateTime> publicKeysCachedTime;

    public OpenIdTokenConfig(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.publicKeysCachedTime = new ConcurrentHashMap<>();
        this.googlePublicKeys = new GooglePublicKeys();
        this.kakaoPublicKeys = new KakaoPublicKeys();
    }

    public Key getPublicKey(String keyId, OAuth2Provider oAuth2Provider) {

        if (OAuth2Provider.KAKAO.equals(oAuth2Provider)) {
            //시간 체크
            if (isKakaoPublicKeyCacheExpired()) {
                //만료되면 공개키 최신화
                refreshKakaoPublicKeys();
            }
            KeyFactory keyFactory = null;
            try {
                keyFactory = KeyFactory.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            //공개키 조회
            return this.kakaoPublicKeys.getKey(keyId, keyFactory);
        }

        if (OAuth2Provider.GOOGLE.equals(oAuth2Provider)) {
            return null;
        }

        throw new NotVerifiedRequestFormatException(keyId);
    }

    private void refreshKakaoPublicKeys() {
        this.kakaoPublicKeys = fetchKakaoPublicKey();
        this.publicKeysCachedTime.put(OAuth2Provider.KAKAO, LocalDateTime.now().plusDays(3L));
    }

    private boolean isKakaoPublicKeyCacheExpired() {
        return LocalDateTime.now()
            .isAfter(publicKeysCachedTime.getOrDefault(OAuth2Provider.KAKAO, LocalDateTime.MIN));
    }

    public KakaoPublicKeys fetchKakaoPublicKey() {

        String uri = "https://kauth.kakao.com/.well-known/jwks.json";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        KakaoPublicKeys kakaoPublicKeys = restTemplate.exchange(uri,
            HttpMethod.GET,
            httpEntity, KakaoPublicKeys.class).getBody();

        return kakaoPublicKeys;
    }
}
