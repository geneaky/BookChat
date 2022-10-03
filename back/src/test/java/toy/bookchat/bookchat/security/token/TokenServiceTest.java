package toy.bookchat.bookchat.security.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.token.dto.RefreshTokenRequestDto;
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
    JwtTokenConfig jwtTokenConfig;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    TokenService tokenService;

    @Test
    public void 리프레시토큰이_아직_유효한_경우_엑세스토큰_재발급_성공() throws Exception {
        String refreshToken = getRefreshToken();

        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
            .refreshToken(refreshToken)
            .build();

        tokenService.generateToken(refreshTokenRequestDto);

        verify(jwtTokenProvider).createAccessToken(any(), any(), any());
    }

    @Test
    public void 리프레시토큰의_만료기간이_얼마남지않은경우_리프레시토큰도_재발급() throws Exception {
        String refreshToken = getRefreshToken();

        RefreshTokenRequestDto refreshTokenRequestDto = RefreshTokenRequestDto.builder()
            .refreshToken(refreshToken)
            .build();

        RefreshToken reNewedRefreshToken = RefreshToken.builder()
            .refreshToken(getRefreshToken())
            .userName("test")
            .build();

        when(jwtTokenManager.shouldRefreshTokenBeRenewed(any())).thenReturn(true);
        when(refreshTokenRepository.findByUserName(any())).thenReturn(reNewedRefreshToken);

        Token token = tokenService.generateToken(refreshTokenRequestDto);

        assertThat(refreshToken).isNotEqualTo(token.getRefreshToken());
    }

    private String getRefreshToken() {
        Map<String, Object> claims = new HashMap<>();
        Date date = new Date(new Date().getTime() + 100000L);
        String refreshToken = Jwts.builder()
            .setClaims(claims)
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, "test")
            .compact();
        return refreshToken;
    }
}