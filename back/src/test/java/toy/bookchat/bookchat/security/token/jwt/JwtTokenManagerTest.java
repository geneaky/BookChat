package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    JwtTokenManagerImpl jwtTokenManager;

    @Test
    public void 토큰에서_사용자_이름_추출_성공() throws Exception {

        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(1111111L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(2222222L);

        String userName = "wRPN";
        String userEmail = "C1Rk6A9";

        Token token = jwtTokenProvider.createToken(userName, userEmail, OAuth2Provider.KAKAO);

        String findUserName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            token.getAccessToken());

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

        String findUserEmail = jwtTokenManager.getUserEmailFromToken(token.getAccessToken());

        assertThat(userEmail).isEqualTo(findUserEmail);
    }

    @Test
    public void 토큰에서_provider_type_추출_성공() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(1111111L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(2222222L);

        String userName = "wRPN";
        String userEmail = "C1Rk6A9";

        Token token = jwtTokenProvider.createToken(userName, userEmail, OAuth2Provider.KAKAO);

        OAuth2Provider provider = jwtTokenManager.getOAuth2ProviderFromToken(
            token.getAccessToken());

        assertThat(provider).isEqualTo(OAuth2Provider.KAKAO);
    }

    @Test
    public void 리프레시토큰_재발급_가능여부_판단_성공() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getReissuePeriod()).thenReturn(259200000L);

        String userName = "wRPN";
        String userEmail = "C1Rk6A9";

        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", userName);
        claims.put("email", userEmail);
        claims.put("provider", OAuth2Provider.KAKAO.getValue());

        String token = Jwts.builder()
            .setClaims(claims)
            .setExpiration(new Date(new Date().getTime() + 249200000L))
            .signWith(SignatureAlgorithm.HS256, "test")
            .compact();

        boolean flag = jwtTokenManager.shouldRefreshTokenBeRenewed(token);

        assertThat(flag).isTrue();
    }
}