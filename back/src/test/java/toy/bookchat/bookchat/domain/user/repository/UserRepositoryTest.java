package toy.bookchat.bookchat.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.configuration.TestConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@DataJpaTest
@Import({JpaAuditingConfig.class,TestConfig.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 사용자_이메일_OAuth2Provider를_구분으로_조회_성공() throws Exception {

        User user1 = User.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.KAKAO)
            .build();

        User user2 = User.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.GOOGLE)
            .build();

        userRepository.save(user1);
        userRepository.save(user2);

        Optional<User> kakaoUser = userRepository.findByEmailAndProvider(
            "kaktus418@gmail.com", OAuth2Provider.KAKAO);

        Optional<User> googleUser = userRepository.findByEmailAndProvider(
            "kaktus418@gmail.com", OAuth2Provider.GOOGLE);

        assertThat(kakaoUser).isNotEqualTo(googleUser);
    }

    @Test
    public void 사용자_nickname_존재시_true_반환_성공() throws Exception {
        User user = User.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.KAKAO)
            .nickname("nickname")
            .build();

        userRepository.save(user);

        boolean result = userRepository.existsByNickname("nickname");

        assertThat(result).isTrue();
    }

    @Test
    public void 사용자_nickname_존재하지않을시_false_반환_성공() throws Exception {
        boolean result = userRepository.existsByNickname("nickname");

        assertThat(result).isFalse();
    }
}