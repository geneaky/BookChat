package toy.bookchat.bookchat.domain.device.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.device.DeviceEntity;
import toy.bookchat.bookchat.domain.device.repository.query.DeviceQueryRepository;
import toy.bookchat.bookchat.domain.user.UserEntity;

public interface DeviceRepository extends DeviceQueryRepository, JpaRepository<DeviceEntity, Long> {

    Optional<DeviceEntity> findByUserEntity(UserEntity userEntity);
}
