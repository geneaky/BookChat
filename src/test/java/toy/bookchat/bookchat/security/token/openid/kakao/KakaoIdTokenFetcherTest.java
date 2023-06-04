package toy.bookchat.bookchat.security.token.openid.kakao;

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
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.exception.unauthorized.DeniedTokenException;
import toy.bookchat.bookchat.exception.unauthorized.ExpiredTokenException;
import toy.bookchat.bookchat.security.token.openid.KakaoIdToken;
import toy.bookchat.bookchat.security.token.openid.OpenIdTestUtil;

@ExtendWith(MockitoExtension.class)
class KakaoIdTokenFetcherTest {

    @InjectMocks
    KakaoIdTokenFetcherImpl kakaoIdTokenFetcher;
    OpenIdTestUtil openIdTestUtil;
    @Mock
    private OAuth2Properties oAuth2Properties;
    @Mock
    private KakaoPublicKeyFetcher kakaoPublicKeyFetcher;
    private String appKey = "testAppKey";

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

    private String getMockOpenIdToken(PrivateKey privateKey) {
        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(getClaims())
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();
        return token;
    }

    private Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1234");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("email", "test@naver.com");
        claims.put("aud", appKey);
        return claims;
    }

    @Test
    void idToken으로_kakaoIdToken_생성_성공() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = getMockOpenIdToken(privateKey);
        KakaoIdToken realKakaoIdToken = KakaoIdToken.from("1234", "test@naver.com");
        when(oAuth2Properties.getKakaoAppKey()).thenReturn(appKey);
        when(kakaoPublicKeyFetcher.getPublicKey(any(), any())).thenReturn(
            publicKey);
        KakaoIdToken expectKakaoIdToken = kakaoIdTokenFetcher.fetchKakaoIdToken(token);

        assertThat(expectKakaoIdToken).isEqualTo(realKakaoIdToken);
    }

    @Test
    void 만료된_토큰으로_처리_요청시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = Jwts.builder()
            .setSubject("1234")
            .setHeaderParam(KEY_ID, "abcdedf")
            .setIssuer("https://kauth.kakao.com")
            .setAudience(appKey)
            .setExpiration(new Date(0))
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(kakaoPublicKeyFetcher.getPublicKey(any(), any())).thenReturn(
            publicKey);

        assertThatThrownBy(() -> {
            kakaoIdTokenFetcher.fetchKakaoIdToken(token);
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    void 임의로_수정한_토큰으로_처리_요청시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        Map<String, Object> claims = getClaims();
        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(kakaoPublicKeyFetcher.getPublicKey(any(), any())).thenReturn(
            publicKey);

        assertThatThrownBy(() -> {
            kakaoIdTokenFetcher.fetchKakaoIdToken(token + "test");
        }).isInstanceOf(DeniedTokenException.class);
    }

    @Test
    void 지원하지않는_발급기관일경우_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1234");
        claims.put("iss", "notsupportedprovider");
        claims.put("email", "test@naver.com");
        claims.put("aud", appKey);

        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(oAuth2Properties.getKakaoAppKey()).thenReturn(appKey);
        when(kakaoPublicKeyFetcher.getPublicKey(any(), any())).thenReturn(
            publicKey);

        assertThatThrownBy(() -> {
            kakaoIdTokenFetcher.fetchKakaoIdToken(token);
        }).isInstanceOf(DeniedTokenException.class);
    }

    @Test
    void idToken에서_이메일_없을시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1234");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("aud", appKey);

        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(kakaoPublicKeyFetcher.getPublicKey(any(), any())).thenReturn(
            publicKey);

        assertThatThrownBy(() -> {
            kakaoIdTokenFetcher.fetchKakaoIdToken(token);
        }).isInstanceOf(DeniedTokenException.class);
    }

    @Test
    void idToken에서_sub_정보_없을시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("email", "test@naver.com");
        claims.put("aud", appKey);

        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        when(kakaoPublicKeyFetcher.getPublicKey(any(), any())).thenReturn(
            publicKey);

        assertThatThrownBy(() -> {
            kakaoIdTokenFetcher.fetchKakaoIdToken(token);
        }).isInstanceOf(DeniedTokenException.class);
    }
}