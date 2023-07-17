package toy.bookchat.bookchat.domain.device.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.repository.query.DeviceQueryRepository;
import toy.bookchat.bookchat.domain.user.User;

public interface DeviceRepository extends DeviceQueryRepository, JpaRepository<Device, Long> {

    Optional<Device> findByUser(User user);
}
