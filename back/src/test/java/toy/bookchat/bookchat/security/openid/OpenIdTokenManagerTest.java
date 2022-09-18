package toy.bookchat.bookchat.security.openid;

import static io.jsonwebtoken.JwsHeader.KEY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;

@ExtendWith(MockitoExtension.class)
class OpenIdTokenManagerTest {

    @Mock
    OpenIdTokenConfig openIdTokenConfig;
    @InjectMocks
    OpenIdTokenManager openIdTokenManager;

    OpenIdTestUtil openIdTestUtil;

    @BeforeEach
    public void init() throws FileNotFoundException {
        //openssl pkcs8 -topk8 -inform PEM -in private_key.pem -out token_key.pem -nocrypt 생성
        openIdTestUtil = new OpenIdTestUtil(
            "src/test/java/toy/bookchat/bookchat/security/openid/token_key.pem",
            "src/test/java/toy/bookchat/bookchat/security/openid/openidRSA256-public.pem");
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

    @Test
    public void 토큰에서_사용자_원천_회원번호_추출_성공() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = getMockOpenIdToken(privateKey);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThat(
            openIdTokenManager.getOAuth2MemberNumberFromOpenIdToken(token, "kakao")).isEqualTo(
            "1234kakao");
    }

    private String getMockOpenIdToken(PrivateKey privateKey) {
        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setSubject("1234")
            .setIssuer("https://kauth.kakao.com")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();
        return token;
    }

    @Test
    public void 만료된_토큰으로_처리_요청시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = Jwts.builder()
            .setSubject("1234")
            .setHeaderParam(KEY_ID, "abcdedf")
            .setIssuer("https://kauth.kakao.com")
            .setExpiration(new Date(0))
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromOpenIdToken(token, "kakao");
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    public void 임의로_수정한_토큰으로_처리_요청시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = getMockOpenIdToken(privateKey);

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromOpenIdToken(token + "test", "kakao");
        }).isInstanceOf(DenidedTokenException.class);
    }

    @Test
    public void 발급_인증기관_정보_없을시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = Jwts.builder()
            .setSubject("1234")
            .setHeaderParam(KEY_ID, "abcdedf")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromOpenIdToken(token, "kakao");
        }).isInstanceOf(DenidedTokenException.class);
    }

    // TODO: 2022/09/16 token길이 테스트 3part가 있어야함 header payload signature

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
}