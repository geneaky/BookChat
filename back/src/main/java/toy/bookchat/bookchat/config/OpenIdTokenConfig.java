package toy.bookchat.bookchat.config;

import java.math.BigInteger;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import toy.bookchat.bookchat.security.exception.NotVerifiedRequestFormatException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.keys.GooglePublicKeys;
import toy.bookchat.bookchat.security.token.openid.keys.KakakoPublicKey;
import toy.bookchat.bookchat.security.token.openid.keys.KakaoPublicKeys;

@Component
public class OpenIdTokenConfig {

    private final RestTemplate restTemplate;
    private final KakaoPublicKeys kakaoPublicKeys;
    private final GooglePublicKeys googlePublicKeys;
    private final ConcurrentHashMap<String, LocalDateTime> publicKeysCachedTime;

    public OpenIdTokenConfig(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.publicKeysCachedTime = new ConcurrentHashMap<>();
        this.googlePublicKeys = searchGooglePublicKeys();
        this.kakaoPublicKeys = searchKakaoPublicKeys();
    }

    private KakaoPublicKeys searchKakaoPublicKeys() {
        publicKeysCachedTime.put("KAKAO", LocalDateTime.now());
        return null;
    }

    private GooglePublicKeys searchGooglePublicKeys() {
        publicKeysCachedTime.put("GOOGLE", LocalDateTime.now());
        return null;
    }

    public Key getPublicKey(String keyId, OAuth2Provider oAuth2Provider) {

        if (OAuth2Provider.KAKAO.equals(oAuth2Provider)) {
            for (KakakoPublicKey publicKey : this.kakaoPublicKeys.getKeys()) {
                return null;
            }
        }

        if (OAuth2Provider.GOOGLE.equals(oAuth2Provider)) {
            return null;
        }

        throw new NotVerifiedRequestFormatException(keyId);
    }

    private void something() {
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);

//        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        BigInteger modulus = new BigInteger(1, Base64Utils.decode("n".getBytes()));
        BigInteger exponent = new BigInteger(1, Base64Utils.decode("e".getBytes()));
//        PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));

    }
}
