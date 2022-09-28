package toy.bookchat.bookchat.security.token.jwt;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class JwtTokenTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    JwtTokenProvider jwtTokenProvider;

    @Test
    public void 만료된_토큰에서_사용자이름_추출시_예외발생() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(0L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(0L);

        String userName = "74X1C6c3";
        String userEmail = "NwFcVZ";

        Token token = jwtTokenProvider.createToken(userName, userEmail, OAuth2Provider.GOOGLE);

        Assertions.assertThatThrownBy(() -> {
            JwtToken jwtToken = JwtToken.of(token.getAccessToken());
            jwtToken.getOAuth2MemberNumber(jwtTokenConfig.getSecret());
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    public void 유효하지않은_토큰에서_사용자이름_추출시_예외발생() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(8888888L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(999999999L);

        String userName = "74X1C6c3";
        String userEmail = "NwFcVZ";

        Token token = jwtTokenProvider.createToken(userName, userEmail, OAuth2Provider.GOOGLE);

        Assertions.assertThatThrownBy(() -> {
            JwtToken jwtToken = JwtToken.of(token.getAccessToken() + "bug");
            jwtToken.getOAuth2MemberNumber(jwtTokenConfig.getSecret());
        }).isInstanceOf(DenidedTokenException.class);
    }

    @Test
    public void 일치하지않은_secretkey로_토큰개방시_예외발생() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(888888888L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(999999999L);

        String userName = "74X1C6c3";
        String userEmail = "NwFcVZ";

        Token token = jwtTokenProvider.createToken(userName, userEmail, OAuth2Provider.GOOGLE);

        Assertions.assertThatThrownBy(() -> {
            JwtToken jwtToken = JwtToken.of(token.getAccessToken());
            jwtToken.getOAuth2MemberNumber("bug");
        }).isInstanceOf(DenidedTokenException.class);
    }
}