package toy.bookchat.bookchat.security.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequest;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.token.jwt.RefreshToken;
import toy.bookchat.bookchat.security.token.jwt.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Spy
    JwtTokenManager jwtTokenManager;
    @Mock
    UserRepository userRepository;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    TokenService tokenService;

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

    @Test
    void 리프레시토큰이_아직_유효한_경우_엑세스토큰_재발급_성공() throws Exception {
        String refreshToken = getRefreshToken();

        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
            .refreshToken(refreshToken)
            .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(getUser()));
        tokenService.generateToken(refreshTokenRequest);

        verify(jwtTokenProvider).createAccessToken(any());
    }

    @Test
    void 리프레시토큰의_만료기간이_얼마남지않은경우_리프레시토큰도_재발급() throws Exception {
        String refreshToken = getRefreshToken();

        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
            .refreshToken(refreshToken)
            .build();

        RefreshToken reNewedRefreshToken = RefreshToken.builder()
            .refreshToken(getRefreshToken())
            .userId(1L)
            .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(getUser()));
        when(jwtTokenManager.shouldRefreshTokenBeRenew(any())).thenReturn(true);
        when(refreshTokenRepository.findByUserId(any())).thenReturn(
            Optional.of(reNewedRefreshToken));

        Token token = tokenService.generateToken(refreshTokenRequest);

        assertThat(refreshToken).isNotEqualTo(token.getRefreshToken());
    }

    @Test
    void 리프레시토큰_갱신시도시_해당토큰이_저장되어있지_않다면_예외발생() throws Exception {
        String refreshToken = getRefreshToken();

        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
            .refreshToken(refreshToken)
            .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(getUser()));
        when(jwtTokenManager.shouldRefreshTokenBeRenew(any())).thenReturn(true);

        assertThatThrownBy(() -> {
            tokenService.generateToken(refreshTokenRequest);
        }).isInstanceOf(IllegalStateException.class);
    }

    private String getRefreshToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        Date date = new Date(new Date().getTime() + 100000L);
        String refreshToken = Jwts.builder()
            .setClaims(claims)
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, "test")
            .compact();
        return refreshToken;
    }
}