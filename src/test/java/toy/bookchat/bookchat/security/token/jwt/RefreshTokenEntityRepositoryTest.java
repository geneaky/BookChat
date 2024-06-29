package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import toy.bookchat.bookchat.domain.RepositoryTest;

class RefreshTokenEntityRepositoryTest extends RepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void 리프레시토큰_저장_성공() throws Exception {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(1L, "3L2");

        refreshTokenRepository.save(refreshTokenEntity);
        refreshTokenRepository.flush();

        RefreshTokenEntity findToken = refreshTokenRepository.findById(refreshTokenEntity.getId()).get();

        assertThat(refreshTokenEntity).isEqualTo(findToken);
    }

    @Test
    void 사용자_리프레시토큰_중복_저장_실패() throws Exception {
        RefreshTokenEntity refreshTokenEntity1 = new RefreshTokenEntity(1L, "3L2");
        refreshTokenRepository.save(refreshTokenEntity1);

        RefreshTokenEntity refreshTokenEntity2 = new RefreshTokenEntity(1L, "3L3");
        assertThatThrownBy(() -> refreshTokenRepository.save(refreshTokenEntity2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 리프레시토큰_이름으로_조회_성공() throws Exception {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(1L, "aFK0");

        refreshTokenRepository.save(refreshTokenEntity);
        refreshTokenRepository.flush();

        RefreshTokenEntity findRefreshTokenEntity = refreshTokenRepository.findByUserId(
            refreshTokenEntity.getUserId()).get();

        assertThat(refreshTokenEntity).isEqualTo(findRefreshTokenEntity);
    }
}