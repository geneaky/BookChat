package toy.bookchat.bookchat.security.token.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.configuration.TestConfig;

@DataJpaTest
@Import({JpaAuditingConfig.class, TestConfig.class})
class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    public void 리프레시토큰_저장_성공() throws Exception {
        RefreshToken refreshToken = new RefreshToken("v4cB4utk", "3L2");

        refreshTokenRepository.save(refreshToken);
        refreshTokenRepository.flush();

        RefreshToken findToken = refreshTokenRepository.findById(refreshToken.getId()).get();

        assertThat(refreshToken).isEqualTo(findToken);
    }

    @Test
    public void 리프레시토큰_이름으로_조회_성공() throws Exception {
        RefreshToken refreshToken = new RefreshToken("aEKrR", "aFK0");

        refreshTokenRepository.save(refreshToken);
        refreshTokenRepository.flush();

        RefreshToken findRefreshToken = refreshTokenRepository.findByUserName("aEKrR");

        assertThat(refreshToken).isEqualTo(findRefreshToken);
    }
}