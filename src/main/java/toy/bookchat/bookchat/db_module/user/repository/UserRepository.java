package toy.bookchat.bookchat.db_module.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.common.Status;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByNameAndStatus(String name, Status status);

    Optional<UserEntity> findByName(String name);

    Optional<UserEntity> findByEmailAndProvider(String email, OAuth2Provider provider);

    boolean existsByNickname(String nickname);

    Optional<UserEntity> findByIdAndStatus(Long userId, Status status);
}
