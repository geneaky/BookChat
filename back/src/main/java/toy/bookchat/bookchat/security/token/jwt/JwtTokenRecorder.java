package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.user.api.dto.Token;

@Component
public class JwtTokenRecorder {

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenRecorder(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void record(String userName, Token token) {

    }
}
