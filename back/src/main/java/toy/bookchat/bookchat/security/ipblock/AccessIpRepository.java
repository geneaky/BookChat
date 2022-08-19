package toy.bookchat.bookchat.security.ipblock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessIpRepository extends JpaRepository<AccessIp, String> {
}
