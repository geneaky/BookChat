package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class JwtTokenManagerTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    JwtTokenManager jwtTokenManager;

    @Test
    public void 토큰에서_사용자_이름_추출_성공() throws Exception {

        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(1111111L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(2222222L);

        String userName = "wRPN";
        String userEmail = "C1Rk6A9";

        Token token = jwtTokenProvider.createToken(userName, userEmail, OAuth2Provider.KAKAO);

        String findUserName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            token.getAccessToken(), null);

        assertThat(userName).isEqualTo(findUserName);
    }

    @Test
    public void 토큰에서_사용자_이메일_추출_성공() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(1111111L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(2222222L);

        String userName = "wRPN";
        String userEmail = "C1Rk6A9";

        Token token = jwtTokenProvider.createToken(userName, userEmail, OAuth2Provider.KAKAO);

        String findUserEmail = jwtTokenManager.getUserEmailFromToken(token.getAccessToken(),
            null);

        assertThat(userEmail).isEqualTo(findUserEmail);
    }


}