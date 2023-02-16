package toy.bookchat.bookchat.security.token.openid;

import static io.jsonwebtoken.JwsHeader.KEY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.exception.security.IllegalStandardTokenException;
import toy.bookchat.bookchat.exception.security.NotSupportedOAuth2ProviderException;

@ExtendWith(MockitoExtension.class)
class KakaoIdTokenTest {

    OpenIdTestUtil openIdTestUtil;

    private String appKey = "testAppKey";

    private Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1234");
        claims.put("iss", "https://kauth.kakao.com");
        claims.put("email", "test@naver.com");
        claims.put("aud", appKey);
        return claims;
    }

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

    private String getMockIdToken(PrivateKey privateKey) {
        Map<String, Object> claims = getClaims();

        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();
        return token;
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

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token);

        assertThatThrownBy(() -> {
            kakaoIdToken.getOAuth2MemberNumber(publicKey, appKey);
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    void 임의로_수정한_토큰으로_처리_요청시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = getMockIdToken(privateKey);

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token + "test");

        assertThatThrownBy(() -> {
            kakaoIdToken.getOAuth2MemberNumber(publicKey, appKey);
        }).isInstanceOf(DenidedTokenException.class);
    }

    @Test
    void 발급_인증기관_정보_없을시_예외발생() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        String token = Jwts.builder()
            .setSubject("1234")
            .setAudience(appKey)
            .setHeaderParam(KEY_ID, "abcdedf")
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token);

        assertThatThrownBy(() -> {
            kakaoIdToken.getOAuth2MemberNumber(publicKey, appKey);
        }).isInstanceOf(IllegalStandardTokenException.class);
    }

    @Test
    void 발급기관_kakao일때_회원번호kakao반환() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        Map<String, Object> claims = getClaims();

        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token);

        String memberNumberWithProviderType = kakaoIdToken.getOAuth2MemberNumber(publicKey, appKey);

        assertThat(memberNumberWithProviderType).isEqualTo("1234kakao");
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

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token);

        assertThatThrownBy(() -> {
            kakaoIdToken.getOAuth2MemberNumber(publicKey, appKey);
        }).isInstanceOf(NotSupportedOAuth2ProviderException.class);
    }

    @Test
    void openidtoken에서_이메일_추출_성공() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKey();

        Map<String, Object> claims = getClaims();

        String token = Jwts.builder()
            .setHeaderParam(KEY_ID, "abcdedf")
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token);

        String email = kakaoIdToken.getEmail(publicKey, appKey);

        assertThat(email).isEqualTo("test@naver.com");
    }

    @Test
    void openidtoken에서_이메일_없을시_예외발생() throws Exception {
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

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token);

        assertThatThrownBy(() -> {
            kakaoIdToken.getEmail(publicKey, appKey);
        }).isInstanceOf(IllegalStandardTokenException.class);
    }

    @Test
    void openidtoken에서_sub_정보_없을시_예외발생() throws Exception {
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

        KakaoIdToken kakaoIdToken = KakaoIdToken.of(token);

        assertThatThrownBy(() -> {
            kakaoIdToken.getOAuth2MemberNumber(publicKey, appKey);
        }).isInstanceOf(IllegalStandardTokenException.class);
    }
}