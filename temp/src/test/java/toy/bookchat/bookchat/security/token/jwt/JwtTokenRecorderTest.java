package toy.bookchat.bookchat.security.token.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtTokenRecorderTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    JwtTokenRecorder jwtTokenRecorder;

    @Test
    void 리프레시토큰_저장_성공() throws Exception {
        jwtTokenRecorder.record(1L, "refreshToken");

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}