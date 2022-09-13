package toy.bookchat.bookchat.security.openid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.impl.DefaultHeader;
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
    OpenIdTestUtil openIdTestUtil = new OpenIdTestUtil(
        "src/test/java/toy/bookchat/bookchat/security/openid/token_key.pem",
        "src/test/java/toy/bookchat/bookchat/security/openid/openidRSA256-public.pem");

    OpenIdTokenManagerTest() throws FileNotFoundException {
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

        PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);

        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        String token = Jwts.builder()
            .setSubject("1234")
            .setIssuer("https://kauth.kakao.com")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(openIdTokenConfig.getPublicKey(any(),any())).thenReturn(publicKey);

        assertThat(openIdTokenManager.getOAuth2MemberNumberFromRequest(token,"kakao")).isEqualTo(
            "1234kakao");
    }

    @Test
    public void 만료된_토큰으로_처리_요청시_예외발생() throws Exception {
        PKCS8EncodedKeySpec priavteKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);

        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(priavteKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        String token = Jwts.builder()
            .setSubject("1234")
            .setIssuer("https://kauth.kakao.com")
            .setExpiration(new Date(0))
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(openIdTokenConfig.getPublicKey(any(),any())).thenReturn(publicKey);

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromRequest(token, "kakao");
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    public void 임의로_수정한_토큰으로_처리_요청시_예외발생() throws Exception {
        PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);

        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        String token = Jwts.builder()
            .setSubject("1234")
            .setIssuer("https://kauth.kakao.com")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(openIdTokenConfig.getPublicKey(any(),any())).thenReturn(publicKey);

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromRequest(token + "test","kakao");
        }).isInstanceOf(DenidedTokenException.class);
    }

    @Test
    public void 발급_인증기관_정보_없을시_예외발생() throws Exception {
        PKCS8EncodedKeySpec privateKeySpec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);

        X509EncodedKeySpec publicKeySpec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        String token = Jwts.builder()
            .setSubject("1234")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(openIdTokenConfig.getPublicKey(any(), any())).thenReturn(publicKey);

        assertThatThrownBy(() -> {
            openIdTokenManager.getOAuth2MemberNumberFromRequest(token,"kakao");
        }).isInstanceOf(DenidedTokenException.class);
    }
}