package toy.bookchat.bookchat.security.token.jwt;

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
import toy.bookchat.bookchat.security.token.openid.OpenIdTestUtil;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class JwtTokenProviderTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    JwtTokenProvider tokenProvider = new JwtTokenProvider(jwtTokenConfig);


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