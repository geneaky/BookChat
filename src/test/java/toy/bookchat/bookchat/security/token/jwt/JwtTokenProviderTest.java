package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    JwtTokenProperties jwtTokenProperties;

    @InjectMocks
    JwtTokenManagerImpl jwtTokenManager;
    @InjectMocks
    JwtTokenProvider tokenProvider;

    private User getUser() {
        return User.builder()
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("google123")
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .provider(OAuth2Provider.GOOGLE)
            .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.ART))
            .build();
    }

    @Test
    void 토큰을_생성_성공() throws Exception {
        when(jwtTokenProperties.getAccessTokenExpiredTime()).thenReturn(99956L);
        when(jwtTokenProperties.getRefreshTokenExpiredTime()).thenReturn(991628L);
        when(jwtTokenProperties.getSecret()).thenReturn("test");
        Token token = tokenProvider.createToken(getUser());

        String userName = jwtTokenManager.getOAuth2MemberNumberFromToken(
            token.getAccessToken());

        assertThat(userName).isEqualTo("google123");
    }
}