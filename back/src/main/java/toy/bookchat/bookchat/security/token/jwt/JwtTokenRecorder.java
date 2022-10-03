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
    public void record(String userName, String refreshToken) {
        /* TODO: 2022-09-29 조회해보고 있으면 갱신해야됨 없으면 신규 등록
         */
        RefreshToken token = new RefreshToken(userName, refreshToken);

        refreshTokenRepository.save(token);
    }
}
