package toy.bookchat.bookchat.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    JwtTokenProvider tokenProvider = new JwtTokenProvider("hihi", 860000L);

    private String getToken() {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);

        Map<String, Object> map = new HashMap<>();
        map.put(JwtTokenProvider.EMAIL, "kaktus418@gmail.com");
        map.put(JwtTokenProvider.OAUTH2_PROVIDER, "kakao");

        Map<String, Object> oAuth2Provider = new HashMap<>();
        oAuth2Provider.put(JwtTokenProvider.KAKAO_ACCOUNT, map);
        oAuth2Provider.put(JwtTokenProvider.SOCIAL_TYPE, OAuth2Provider.kakao);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getAttributes()).thenReturn(oAuth2Provider);

        return tokenProvider.createToken(authentication);
    }

    @Test
    public void 토큰을_생성_성공() throws Exception {
        String token = getToken();

        assertThat(token).isNotBlank();
        assertThat(token).isInstanceOf(String.class);

    }

    @Test
    public void 토큰에서_이메일_추출_성공() throws Exception {
        String token = getToken();

        String email = tokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("kaktus418@gmail.com");
    }

    @Test
    public void 토큰에서_Oauth2Provider_추출_성공() throws Exception {
        String token = getToken();

        OAuth2Provider oauth2TokenProvider = tokenProvider.getOauth2TokenProviderFromToken(token);

        assertThat(oauth2TokenProvider).isEqualTo(OAuth2Provider.kakao);
    }

    @Test
    public void 정상_토큰_validation_성공() throws Exception {

        String token = getToken();

        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    /*@TODO
     *   토큰 검증 테스트 추가
     *   유효시간만료, 토큰 수정여부, 토큰 시그니쳐 검증등*/
}