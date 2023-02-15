package toy.bookchat.bookchat.security.token.openid;

import static io.jsonwebtoken.JwsHeader.KEY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.token.openid.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class OpenIdTokenManagerTest {

    @Mock
    OpenIdTokenConfig openIdTokenConfig;
    @InjectMocks
    OpenIdTokenManagerImpl openIdTokenManager;

    OpenIdTestUtil openIdTestUtil;

    @BeforeEach
    public void init() throws FileNotFoundException {
        //openssl pkcs8 -topk8 -inform PEM -in private_key.pem -out token_key.pem -nocrypt 생성
        openIdTestUtil = new OpenIdTestUtil(
            "src/test/java/toy/bookchat/bookchat/security/token/openid/token_key.pem",
            "src/test/java/toy/bookchat/bookchat/security/token/openid/openidRSA256-public.pem");
    }

    private X509EncodedKeySpec getPublicPkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
        throws IOException {
        String publicKey = openIdTestUtil.getPublicKey(9);
        byte[] decodePublicKey = Base64Utils.decode(publicKey.getBytes());
        return new X509EncodedKeySpec(decodePublicKey);
    }

    private PKCS8EncodedKeySpec getPrivatePkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
        throws IOException {
        String privateKey = openIdTestUtil.getPrivateKey(28);
        byte[] decodePrivateKey = Base64Utils.decode(privateKey.getBytes());
        return new PKCS8EncodedKeySpec(
            decodePrivateKey);
    }

    private PublicKey getPublicKey()
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);
        return keyFactory.generatePublic(publicKeySpec);
    }

    private PrivateKey getPrivateKey()
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    @Test
    void 토큰에서_사용자_원천_회원번호_추출_성공() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = getMockOpenIdToken(privateKey);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThat(
            openIdTokenManager.getOAuth2MemberNumberFromIdToken(token,
                OAuth2Provider.KAKAO)).isEqualTo(
            "1234kakao");
    }

    @Test
    void 토큰에서_사용자_원천_이메일_추출_성공() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = getMockOpenIdToken(privateKey);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThat(
            openIdTokenManager.getUserEmailFromToken(token,
                OAuth2Provider.KAKAO)).isEqualTo(
            "test@naver.com");
    }

    private String getMockOpenIdToken(PrivateKey privateKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1234");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("email", "test@naver.com");

        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();
        return "Bearer " + token;
    }
}