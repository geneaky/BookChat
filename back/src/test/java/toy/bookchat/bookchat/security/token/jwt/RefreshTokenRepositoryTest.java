package toy.bookchat.bookchat.security.token.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.configuration.TestConfig;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@Transactional
class RefreshTokenRepositoryTest {

    @MockBean
    JwtTokenConfig jwtTokenConfig;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    public void BaseEntityTest1() throws Exception {
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken("hi")
                .userName("jcr")
                .build();

//        refreshToken.setUpdatedAt(LocalDateTime.now().minusSeconds(10)); //updateable false라서 동작 안함
        refreshToken.setCreatedAt(LocalDateTime.now().minusSeconds(10));
        RefreshToken save = refreshTokenRepository.save(refreshToken);
        refreshTokenRepository.flush();

        System.out.println("save::createdAt " + save.getCreatedAt());
        System.out.println("save::updatedAt " + save.getUpdatedAt());


//        RefreshToken token = refreshTokenRepository.findById(refreshToken.getId()).get();

//        token.setUserName("change");

//        refreshTokenRepository.flush();

        RefreshToken token2 = refreshTokenRepository.findById(refreshToken.getId()).get();

        System.out.println("save2::createdAt " + token2.getCreatedAt());
        System.out.println("save2::updatedAt " + token2.getUpdatedAt());

    }

}