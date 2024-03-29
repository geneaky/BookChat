package toy.bookchat.bookchat.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.common.Status;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameAndStatus(String name, Status status);

    Optional<User> findByName(String name);

    Optional<User> findByEmailAndProvider(String email, OAuth2Provider provider);

    boolean existsByNickname(String nickname);

    Optional<User> findByIdAndStatus(Long userId, Status status);
}
