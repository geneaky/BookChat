package toy.bookchat.bookchat.domain.device.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.device.DeviceEntity;
import toy.bookchat.bookchat.domain.user.UserEntity;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.token.jwt.RefreshTokenEntity;
import toy.bookchat.bookchat.security.token.jwt.RefreshTokenRepository;

class DeviceEntityRepositoryTest extends RepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    void 사용자_디바이스_조회() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .nickname("Khl")
            .build();

        userRepository.save(userEntity);

        DeviceEntity deviceEntity = DeviceEntity.builder()
            .userEntity(userEntity)
            .deviceToken("1F46c3Hr")
            .fcmToken("gFFS190")
            .build();

        deviceRepository.save(deviceEntity);

        DeviceEntity findDeviceEntity = deviceRepository.findByUserEntity(userEntity).get();

        assertThat(deviceEntity).isEqualTo(findDeviceEntity);
    }

    @Test
    void 사용자_디바이스_삭제() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .nickname("Khl")
            .build();

        userRepository.save(userEntity);

        DeviceEntity deviceEntity = DeviceEntity.builder()
            .userEntity(userEntity)
            .deviceToken("1F46c3Hr")
            .fcmToken("gFFS190")
            .build();

        deviceRepository.save(deviceEntity);

        em.clear();

        deviceRepository.deleteByUserId(userEntity.getId());

        Optional<DeviceEntity> findDevice = deviceRepository.findByUserEntity(userEntity);

        assertThat(findDevice).isEmpty();
    }

    @Test
    @Disabled
    void 만료된_fcm토큰_삭제() throws Exception {
        UserEntity userEntity = UserEntity.builder()
            .nickname("Khl")
            .build();
        userRepository.save(userEntity);

        DeviceEntity deviceEntity = DeviceEntity.builder()
            .deviceToken("nSPKVZh")
            .fcmToken("z2G1")
            .userEntity(userEntity)
            .build();
        deviceRepository.save(deviceEntity);

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
            .userId(userEntity.getId())
            .build();
        refreshTokenEntity.setUpdatedAt(LocalDateTime.now().minusMonths(2).minusHours(2));
        refreshTokenRepository.save(refreshTokenEntity);

        em.flush();
        em.clear();

        deviceRepository.deleteExpiredFcmToken();
        Optional<DeviceEntity> optionalDevice = deviceRepository.findById(deviceEntity.getId());

        assertThat(optionalDevice).isEmpty();
    }
}