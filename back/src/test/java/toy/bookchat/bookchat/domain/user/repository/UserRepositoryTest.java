package toy.bookchat.bookchat.domain.user.repository;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.domain.configuration.TestConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@DataJpaTest
@Import(TestConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 사용자_이메일_OAuth2Provider를_구분으로_조회_성공() throws Exception {

        User user1 = User.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.kakao)
            .build();

        User user2 = User.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.google)
            .build();

        userRepository.save(user1);
        userRepository.save(user2);

        Optional<User> kakaoUser = userRepository.findByEmailAndProvider(
            "kaktus418@gmail.com", OAuth2Provider.kakao);

        Optional<User> googleUser = userRepository.findByEmailAndProvider(
            "kaktus418@gmail.com", OAuth2Provider.google);

        Assertions.assertThat(kakaoUser).isNotEqualTo(googleUser);
    }
}