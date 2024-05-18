package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.response.Token;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;

@ExtendWith(MockitoExtension.class)
class JwtTokenManagerTest {

    @Mock
    JwtTokenProperties jwtTokenProperties;
    @InjectMocks
    JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    JwtTokenManagerImpl jwtTokenManager;

    private User getUser() {
        return User.builder()
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .provider(OAuth2Provider.KAKAO)
            .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.ART))
            .build();
    }

    private TokenPayload getTokenPayload(User user) {
        return TokenPayload.of(user.getId(), user.getName(),
            user.getNickname(),
            user.getEmail(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            user.getRole());
    }

    @Test
    void 토큰에서_사용자_이름_추출_성공() throws Exception {
        generalTokenConfigContext();
        User user = getUser();
        Token token = jwtTokenProvider.createToken(getUser());
        String findUserName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            token.getAccessToken());

        assertThat(user.getName()).isEqualTo(findUserName);
    }

    private void generalTokenConfigContext() {
        when(jwtTokenProperties.getSecret()).thenReturn("test");
        when(jwtTokenProperties.getAccessTokenExpiredTime()).thenReturn(1111111L);
        when(jwtTokenProperties.getRefreshTokenExpiredTime()).thenReturn(2222222L);
    }

    @Test
    void 토큰에서_사용자_이메일_추출_성공() throws Exception {
        generalTokenConfigContext();
        User user = getUser();
        Token token = jwtTokenProvider.createToken(getUser());
        String findUserEmail = jwtTokenManager.getUserEmailFromToken(token.getAccessToken());

        assertThat(user.getEmail()).isEqualTo(findUserEmail);
    }

    @Test
    void 토큰에서_provider_type_추출_성공() throws Exception {
        User user = getUser();

        assertThat(user.getProvider()).isEqualTo(OAuth2Provider.KAKAO);
    }

    @Test
    void 리프레시토큰_재발급_가능여부_판단_성공() throws Exception {
        when(jwtTokenProperties.getSecret()).thenReturn("test");
        when(jwtTokenProperties.getReissuePeriod()).thenReturn(259200000L);

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

        boolean flag = jwtTokenManager.shouldRefreshTokenBeRenew(token);

        assertThat(flag).isTrue();
    }

    @Test
    void 토큰에서_사용자_id_추출_성공() throws Exception {
        generalTokenConfigContext();
        Token token = jwtTokenProvider.createToken(getUser());
        Long result = jwtTokenManager.getUserIdFromToken(token.getAccessToken());

        assertThat(result).isEqualTo(1L);
    }

    @Test
    void 토큰에서_TokenPayload_반환_성공() throws Exception {
        generalTokenConfigContext();
        User user = getUser();
        Token token = jwtTokenProvider.createToken(getUser());
        TokenPayload result = jwtTokenManager.getTokenPayloadFromToken(
            token.getAccessToken());
        TokenPayload expect = getTokenPayload(user);

        assertThat(result).isEqualTo(expect);
    }
}