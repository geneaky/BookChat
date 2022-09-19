package toy.bookchat.bookchat.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider.KAKAO_ACCOUNT;

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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Base64Utils;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.openid.OpenIdTestUtil;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenValidationCode;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Disabled
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class JwtTokenProviderTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    JwtTokenProvider tokenProvider = new JwtTokenProvider();

    private X509EncodedKeySpec getPublicPkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
        throws IOException {
        String publicKey = openIdTestUtil.getPublicKey(9);
        byte[] decodePublicKey = Base64Utils.decode(publicKey.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getBytes());
        return spec;
    }

    private PKCS8EncodedKeySpec getPrivatePkcs8EncodedKeySpec(OpenIdTestUtil openIdTestUtil)
        throws IOException {
        String privateKey = openIdTestUtil.getPrivateKey(28);
        byte[] decodePrivateKey = Base64Utils.decode(privateKey.getBytes());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(
            decodePrivateKey);
        return spec;
    }

    private String getKakaoToken(Long expiredTime)
        throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        User user = mock(User.class);

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(JwtTokenProvider.EMAIL, "kaktus418@gmail.com");
        innerMap.put(JwtTokenProvider.OAUTH2_PROVIDER, "kakao");
        OAuth2Provider oAuth2Provider = OAuth2Provider.KAKAO;

        Map<String, Object> outerMap = new HashMap<>();
        outerMap.put(KAKAO_ACCOUNT, innerMap);

        OpenIdTestUtil openIdTestUtil = new OpenIdTestUtil(
            "src/test/java/toy/bookchat/bookchat/security/openid/token_key.pem",
            "src/test/java/toy/bookchat/bookchat/security/openid/openidRSA256-public.pem");

        PKCS8EncodedKeySpec spec = getPrivatePkcs8EncodedKeySpec(openIdTestUtil);

        X509EncodedKeySpec pspec = getPublicPkcs8EncodedKeySpec(openIdTestUtil);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey pk = keyFactory.generatePrivate(spec);
        PublicKey publicKey1 = keyFactory.generatePublic(pspec);

        when(jwtTokenConfig.getSecret()).thenReturn(publicKey1);

        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(expiredTime);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUser()).thenReturn(user);
        when(userPrincipal.getAttributes()).thenReturn(outerMap);
        when(user.getProvider()).thenReturn(oAuth2Provider);

        return tokenProvider.createToken(authentication);
    }

    @Test
    public void 토큰을_생성_성공() throws Exception {
        String token = getKakaoToken(3600L);

        assertThat(token).isNotBlank();
        assertThat(token).isInstanceOf(String.class);

    }

    @Test
    public void 토큰에서_이메일_추출_성공() throws Exception {
        String token = getKakaoToken(3600L);

        String email = tokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("kaktus418@gmail.com");
    }

    @Test
    public void 토큰에서_Oauth2Provider_추출_성공() throws Exception {
        String token = getKakaoToken(3600L);

        OAuth2Provider oauth2TokenProvider = tokenProvider.getOauth2TokenProviderFromToken(token);

        assertThat(oauth2TokenProvider).isEqualTo(OAuth2Provider.KAKAO);
    }

    @Test
    public void 정상_토큰_validation_성공() throws Exception {

        String token = getKakaoToken(3600L);

        assertThat(tokenProvider.validateToken(token)).isEqualTo(JwtTokenValidationCode.ACCESS);
    }

    @Test
    public void 유효시간_만료_토큰_validation_성공() throws Exception {
        String token = getKakaoToken(0L);

        assertThat(tokenProvider.validateToken(token)).isEqualTo(JwtTokenValidationCode.EXPIRED);
    }

    @Test
    public void 발급받은_토큰_임의_수정한_경우_validation_성공() throws Exception {
        String token = getKakaoToken(3600L) + "hahahoho";

        assertThat(tokenProvider.validateToken(token)).isEqualTo(JwtTokenValidationCode.DENIED);
    }
}