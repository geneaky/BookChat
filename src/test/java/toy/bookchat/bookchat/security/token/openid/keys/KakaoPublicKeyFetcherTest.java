package toy.bookchat.bookchat.security.token.openid.keys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class KakaoPublicKeyFetcherTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WebClient webClient;
    @InjectMocks
    KakaoPublicKeyFetcher kakaoPublickeyFetcher;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String apiUri = "https://kauth.kakao.com/.well-known/jwks.json";
    private String result = "{\"keys\": [{\"kid\": \"3f96980381e451efad0d2ddd30e3d3\",\"kty\": \"RSA\",\"alg\": \"RS256\",\"use\": \"sig\",\"n\": \"q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw\",\"e\": \"AQAB\"}, {\"kid\": \"9f252dadd5f233f93d2fa528d12fea\",\"kty\": \"RSA\",\"alg\": \"RS256\",\"use\": \"sig\",\"n\": \"qGWf6RVzV2pM8YqJ6by5exoixIlTvdXDfYj2v7E6xkoYmesAjp_1IYL7rzhpUYqIkWX0P4wOwAsg-Ud8PcMHggfwUNPOcqgSk1hAIHr63zSlG8xatQb17q9LrWny2HWkUVEU30PxxHsLcuzmfhbRx8kOrNfJEirIuqSyWF_OBHeEgBgYjydd_c8vPo7IiH-pijZn4ZouPsEg7wtdIX3-0ZcXXDbFkaDaqClfqmVCLNBhg3DKYDQOoyWXrpFKUXUFuk2FTCqWaQJ0GniO4p_ppkYIf4zhlwUYfXZEhm8cBo6H2EgukntDbTgnoha8kNunTPekxWTDhE5wGAt6YpT4Yw\",\"e\": \"AQAB\"}]}";
    private String n = "q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw";
    private String e = "AQAB";
    private String kid = "3f96980381e451efad0d2ddd30e3d3";

    @Test
    public void kakao_keyId_oAuth2Provider로_요청시_일치하는_publickey반환() throws Exception {

        KakaoPublicKeys kakaoPublicKeys = objectMapper.readValue(result, KakaoPublicKeys.class);

        when(webClient.get().uri(apiUri).retrieve().bodyToMono(KakaoPublicKeys.class)
            .block()).thenReturn(kakaoPublicKeys);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        BigInteger modulus = new BigInteger(1, Base64Utils.decodeFromUrlSafeString(n));
        BigInteger exponent = new BigInteger(1, Base64Utils.decodeFromUrlSafeString(e));
        PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
        PrivateKey privateKey = keyFactory.generatePrivate(
            new RSAPrivateKeySpec(modulus, exponent));

        Key kakaoPublicKey = kakaoPublickeyFetcher.getPublicKey(getMockOpenIdToken(privateKey),
            apiUri);

        assertThat(kakaoPublicKey).isEqualTo(publicKey);
    }

    private String getMockOpenIdToken(PrivateKey privateKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1234");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("email", "test@naver.com");

        String token = Jwts.builder()
            .setHeaderParam("kid", kid)
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();
        return token;
    }
}