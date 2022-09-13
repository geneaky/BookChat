package toy.bookchat.bookchat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import toy.bookchat.bookchat.config.openid.PublicKeys;
import toy.bookchat.bookchat.security.exception.NotVerifiedRequestFormatException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.openid.keys.GooglePublicKeys;
import toy.bookchat.bookchat.security.openid.keys.KakakoPublicKey;
import toy.bookchat.bookchat.security.openid.keys.KakaoPublicKeys;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenIdTokenConfig {

    private final RestTemplate restTemplate;

    private Map<String, LocalDateTime> publicKeysCachedTime = new HashMap<>();

    private final KakaoPublicKeys kakaoPublicKeys;

    private final GooglePublicKeys googlePublicKeys;


    public Key getPublicKey(String keyId, String tokenProvider) {
        if(OAuth2Provider.KAKAO.getValue().equals(keyId)) {
            for(KakakoPublicKey publicKey : this.kakaoPublicKeys.getKeys()) {
                return null;
            }
        } else if (OAuth2Provider.GOOGLE.getValue().equals(keyId)) {
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
