package toy.bookchat.bookchat.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.config.token.openid.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@RestClientTest(OpenIdTokenConfig.class)
@ExtendWith(MockitoExtension.class)
class OpenIdTokenConfigTest {

    @MockBean
    OAuth2Properties oAuth2Properties;
    @Autowired
    OpenIdTokenConfig openIdTokenConfig;
    @Autowired
    private MockRestServiceServer mockServer;

    private String apiUri = "https://kauth.kakao.com/.well-known/jwks.json";
    private String result = "{\"keys\": [{\"kid\": \"3f96980381e451efad0d2ddd30e3d3\",\"kty\": \"RSA\",\"alg\": \"RS256\",\"use\": \"sig\",\"n\": \"q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw\",\"e\": \"AQAB\"}, {\"kid\": \"9f252dadd5f233f93d2fa528d12fea\",\"kty\": \"RSA\",\"alg\": \"RS256\",\"use\": \"sig\",\"n\": \"qGWf6RVzV2pM8YqJ6by5exoixIlTvdXDfYj2v7E6xkoYmesAjp_1IYL7rzhpUYqIkWX0P4wOwAsg-Ud8PcMHggfwUNPOcqgSk1hAIHr63zSlG8xatQb17q9LrWny2HWkUVEU30PxxHsLcuzmfhbRx8kOrNfJEirIuqSyWF_OBHeEgBgYjydd_c8vPo7IiH-pijZn4ZouPsEg7wtdIX3-0ZcXXDbFkaDaqClfqmVCLNBhg3DKYDQOoyWXrpFKUXUFuk2FTCqWaQJ0GniO4p_ppkYIf4zhlwUYfXZEhm8cBo6H2EgukntDbTgnoha8kNunTPekxWTDhE5wGAt6YpT4Yw\",\"e\": \"AQAB\"}]}";
    private String n = "q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw";
    private String e = "AQAB";
    private String kid = "3f96980381e451efad0d2ddd30e3d3";

    @Test
    public void kakao_keyId_oAuth2Provider로_요청시_일치하는_publickey반환() throws Exception {

        when(oAuth2Properties.getKakaoUri()).thenReturn(apiUri);
        mockServer.expect(requestTo(apiUri))
            .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));

        Key kakaoPublicKey = openIdTokenConfig.getPublicKey(kid, OAuth2Provider.KAKAO);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        BigInteger modulus = new BigInteger(1,
            Base64Utils.decodeFromUrlSafeString(n));
        BigInteger exponent = new BigInteger(1,
            Base64Utils.decodeFromUrlSafeString(e));
        PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));

        assertThat(kakaoPublicKey).isEqualTo(publicKey);
    }
}