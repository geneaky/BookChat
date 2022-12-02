package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JwtTokenRecorder {

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenRecorder(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void record(Long userId, String refreshToken) {
        refreshTokenRepository.findByUserId(userId)
            .ifPresentOrElse(r -> r.changeRefreshToken(refreshToken), () -> {
                RefreshToken token = new RefreshToken(userId, refreshToken);
                refreshTokenRepository.save(token);
            });
    }
}
