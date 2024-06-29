package toy.bookchat.bookchat.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static toy.bookchat.bookchat.domain.common.Status.ACTIVE;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

class UserEntityRepositoryTest extends RepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @Test
    void 사용자_이메일_OAuth2Provider를_구분으로_조회_성공() throws Exception {

        UserEntity userEntity1 = UserEntity.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.KAKAO)
            .build();

        UserEntity userEntity2 = UserEntity.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.GOOGLE)
            .build();

        userRepository.save(userEntity1);
        userRepository.save(userEntity2);

        Optional<UserEntity> kakaoUser = userRepository.findByEmailAndProvider(
            "kaktus418@gmail.com", OAuth2Provider.KAKAO);

        Optional<UserEntity> googleUser = userRepository.findByEmailAndProvider(
            "kaktus418@gmail.com", OAuth2Provider.GOOGLE);

        assertThat(kakaoUser).isNotEqualTo(googleUser);
    }

    @Test
    void 사용자_nickname_존재시_true_반환_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .name("user")
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.KAKAO)
            .nickname("nickname")
            .build();

        userRepository.save(userEntity);

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
        UserEntity userEntity = UserEntity.builder()
            .name(userName)
            .email("kaktus418@gmail.com")
            .provider(OAuth2Provider.KAKAO)
            .nickname("nickname")
            .build();

        userRepository.save(userEntity);

        UserEntity findUserEntity = userRepository.findByName(userName).get();

        assertThat(userEntity).isEqualTo(findUserEntity);
    }

    @Test
    void 사용자_삭제_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .email("test@gmail.com")
            .name("testkakao")
            .nickname("nickname")
            .role(ROLE.USER)
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .build();

        userRepository.save(userEntity);

        userRepository.delete(userEntity);

        Optional<UserEntity> ou = userRepository.findById(userEntity.getId());
        assertThat(ou).isNotPresent();
    }

    @Test
    void 변경_요청한_닉네임_이미_있을시_예외발생() throws Exception {
        UserEntity userEntity1 = UserEntity.builder()
            .nickname("user1")
            .build();

        UserEntity userEntity2 = UserEntity.builder()
            .nickname("user2")
            .build();

        userRepository.saveAndFlush(userEntity1);
        userRepository.save(userEntity2);
        userEntity2.changeUserNickname("user1");

        assertThatThrownBy(() -> {
            userRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 이미_존재하는_nickname인_경우_생성_실패() throws Exception {
        UserEntity userEntity1 = UserEntity.builder()
            .nickname("test nickname")
            .build();

        userRepository.save(userEntity1);

        UserEntity userEntity2 = UserEntity.builder()
            .nickname("test nickname")
            .build();
        assertThatThrownBy(() -> userRepository.save(userEntity2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 사용자_계정생성시_기본_상태값_active로_생성_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .nickname("user1")
            .build();

        userRepository.save(userEntity);

        em.flush();
        em.clear();

        UserEntity findUserEntity = userRepository.findById(userEntity.getId()).get();

        assertThat(findUserEntity.getStatus()).isEqualTo(ACTIVE);
    }
}