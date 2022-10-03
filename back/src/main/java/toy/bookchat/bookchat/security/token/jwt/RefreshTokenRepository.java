package toy.bookchat.bookchat.security.token.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByUserName(String username);
}
