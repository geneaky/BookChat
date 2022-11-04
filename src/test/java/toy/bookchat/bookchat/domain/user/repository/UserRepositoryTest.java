package toy.bookchat.bookchat.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.configuration.TestConfig;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@DataJpaTest
@Import({JpaAuditingConfig.class, TestConfig.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_이메일_OAuth2Provider를_구분으로_조회_성공() throws Exception {

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
    void 사용자_nickname_존재시_true_반환_성공() throws Exception {
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
    void 사용자_nickname_존재하지않을시_false_반환_성공() throws Exception {
        boolean result = userRepository.existsByNickname("nickname");

        assertThat(result).isFalse();
    }

    @Test
    void 사용자_이름으로_조회_성공() throws Exception {
        String userName = "KAKAO123456";
        User user = User.builder()
            .name(userName)
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.KAKAO)
            .nickname("nickname")
            .build();

        userRepository.save(user);

        User findUser = userRepository.findByName(userName).get();

        assertThat(user).isEqualTo(findUser);
    }

    @Test
    void 사용자_삭제_성공() throws Exception {
        User user = User.builder()
            .email("test@gmail.com")
            .name("testkakao")
            .nickname("nickname")
            .role(ROLE.USER)
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .build();

        userRepository.save(user);

        userRepository.delete(user);

        Optional<User> ou = userRepository.findById(user.getId());
        assertThat(ou).isNotPresent();
    }

    @Test
    void 변경_요청한_닉네임_이미_있을시_예외발생() throws Exception {
        User user1 = User.builder()
            .nickname("user1")
            .build();

        User user2 = User.builder()
            .nickname("user2")
            .build();

        userRepository.save(user1);
        userRepository.save(user2);

        user2.changeUserNickname("user1");

        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user2);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}