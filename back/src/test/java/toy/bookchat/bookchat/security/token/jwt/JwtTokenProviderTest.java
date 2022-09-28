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
class JwtTokenProviderTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    JwtTokenManager jwtTokenManager;
    @InjectMocks
    JwtTokenProvider tokenProvider;


    @Test
    public void 토큰을_생성_성공() throws Exception {
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(99956L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(991628L);
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        Token token = tokenProvider.createToken("google123", "test@gamil.com",
            OAuth2Provider.GOOGLE);

        String userName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            token.getAccessToken(), null);

        assertThat(userName).isEqualTo("google123");
    }
}