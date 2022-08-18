package toy.bookchat.bookchat.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.security.jwt.JwtTokenProvider.KAKAO_ACCOUNT;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class JwtTokenProviderTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    JwtTokenProvider tokenProvider = new JwtTokenProvider();

    private String getKakaoToken() {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        User user = mock(User.class);

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(JwtTokenProvider.EMAIL, "kaktus418@gmail.com");
        innerMap.put(JwtTokenProvider.OAUTH2_PROVIDER, "kakao");
        OAuth2Provider oAuth2Provider = OAuth2Provider.kakao;

        Map<String, Object> outerMap = new HashMap<>();
        outerMap.put(KAKAO_ACCOUNT, innerMap);

        when(jwtTokenConfig.getSecret()).thenReturn("hihi");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(3600L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUser()).thenReturn(user);
        when(userPrincipal.getAttributes()).thenReturn(outerMap);
        when(user.getProvider()).thenReturn(oAuth2Provider);

        return tokenProvider.createToken(authentication);
    }

    @Test
    public void 토큰을_생성_성공() throws Exception {
        String token = getKakaoToken();

        assertThat(token).isNotBlank();
        assertThat(token).isInstanceOf(String.class);

    }

    @Test
    public void 토큰에서_이메일_추출_성공() throws Exception {
        String token = getKakaoToken();

        String email = tokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("kaktus418@gmail.com");
    }

    @Test
    public void 토큰에서_Oauth2Provider_추출_성공() throws Exception {
        String token = getKakaoToken();

        OAuth2Provider oauth2TokenProvider = tokenProvider.getOauth2TokenProviderFromToken(token);

        assertThat(oauth2TokenProvider).isEqualTo(OAuth2Provider.kakao);
    }

    @Test
    public void 정상_토큰_validation_성공() throws Exception {

        String token = getKakaoToken();

        assertThat(tokenProvider.validateToken(token)).isEqualTo(JwtTokenValidationCode.ACCESS);
    }

  /*      @Test
    public void 토큰_만료시_refreshToken요청_응답_세팅_성공() throws Exception {
        String token = getExpiredToken();

        tokenProvider.validateToken(token);
    }
*/
    /*@TODO
     *   토큰 검증 테스트 추가
     *   유효시간만료, 토큰 수정여부, 토큰 시그니쳐 검증등*/
}