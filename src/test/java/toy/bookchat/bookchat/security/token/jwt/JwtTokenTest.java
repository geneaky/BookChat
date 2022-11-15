package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.TokenPayload;

@ExtendWith(MockitoExtension.class)
class JwtTokenTest {

    @Mock
    JwtTokenConfig jwtTokenConfig;

    @InjectMocks
    JwtTokenProvider jwtTokenProvider;

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
    void 만료된_토큰에서_사용자이름_추출시_예외발생() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(0L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(0L);

        User user = getUser();

        Token token = jwtTokenProvider.createToken(user);

        assertThatThrownBy(() -> {
            JwtToken jwtToken = JwtToken.of(token.getAccessToken());
            jwtToken.getOAuth2MemberNumber(jwtTokenConfig.getSecret());
        }).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    void 유효하지않은_토큰에서_사용자이름_추출시_예외발생() throws Exception {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(8888888L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(999999999L);

        Token token = jwtTokenProvider.createToken(getUser());

        assertThatThrownBy(() -> {
            JwtToken jwtToken = JwtToken.of(token.getAccessToken() + "bug");
            jwtToken.getOAuth2MemberNumber(jwtTokenConfig.getSecret());
        }).isInstanceOf(DenidedTokenException.class);
    }

    @Test
    void 일치하지않은_secretkey로_토큰개방시_예외발생() throws Exception {
        generalTokenConfigContext();

        Token token = jwtTokenProvider.createToken(getUser());

        assertThatThrownBy(() -> {
            JwtToken jwtToken = JwtToken.of(token.getAccessToken());
            jwtToken.getOAuth2MemberNumber("bug");
        }).isInstanceOf(DenidedTokenException.class);
    }

    @Test
    void 토큰에서_userId_추출_성공() throws Exception {
        generalTokenConfigContext();

        Token token = jwtTokenProvider.createToken(getUser());

        Long result = JwtToken.of(token.getAccessToken()).getUserId("test");

        assertThat(result).isEqualTo(1L);
    }

    @Test
    void 토큰에서_TokenPayload_추출_성공() throws Exception {
        generalTokenConfigContext();

        Token token = jwtTokenProvider.createToken(getUser());

        TokenPayload result = JwtToken.of(token.getAccessToken()).getPayload("test");
        TokenPayload expect = getTokenPayload(getUser());

        assertThat(result).isEqualTo(expect);
    }

    private void generalTokenConfigContext() {
        when(jwtTokenConfig.getSecret()).thenReturn("test");
        when(jwtTokenConfig.getAccessTokenExpiredTime()).thenReturn(888888888L);
        when(jwtTokenConfig.getRefreshTokenExpiredTime()).thenReturn(999999999L);
    }
}