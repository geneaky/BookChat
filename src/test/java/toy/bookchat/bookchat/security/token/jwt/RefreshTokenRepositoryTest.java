package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import toy.bookchat.bookchat.domain.RepositoryTest;

@RepositoryTest
class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void 리프레시토큰_저장_성공() throws Exception {
        RefreshToken refreshToken = new RefreshToken(1L, "3L2");

        refreshTokenRepository.save(refreshToken);
        refreshTokenRepository.flush();

        RefreshToken findToken = refreshTokenRepository.findById(refreshToken.getId()).get();

        assertThat(refreshToken).isEqualTo(findToken);
    }

    @Test
    void 사용자_리프레시토큰_중복_저장_실패() throws Exception {
        RefreshToken refreshToken1 = new RefreshToken(1L, "3L2");
        refreshTokenRepository.save(refreshToken1);

        RefreshToken refreshToken2 = new RefreshToken(1L, "3L3");
        assertThatThrownBy(() -> refreshTokenRepository.save(refreshToken2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 리프레시토큰_이름으로_조회_성공() throws Exception {
        RefreshToken refreshToken = new RefreshToken(1L, "aFK0");

        refreshTokenRepository.save(refreshToken);
        refreshTokenRepository.flush();

        RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(
            refreshToken.getUserId()).get();

        assertThat(refreshToken).isEqualTo(findRefreshToken);
    }
}