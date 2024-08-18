package toy.bookchat.bookchat.db_module.device.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.device.repository.query.DeviceQueryRepository;

public interface DeviceRepository extends DeviceQueryRepository, JpaRepository<DeviceEntity, Long> {

  Optional<DeviceEntity> findByUserId(Long userId);
}
