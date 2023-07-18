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
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.token.jwt.RefreshToken;
import toy.bookchat.bookchat.security.token.jwt.RefreshTokenRepository;

@RepositoryTest
class DeviceRepositoryTest {

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
        User user = User.builder()
            .nickname("Khl")
            .build();

        userRepository.save(user);

        Device device = Device.builder()
            .user(user)
            .deviceToken("1F46c3Hr")
            .fcmToken("gFFS190")
            .build();

        deviceRepository.save(device);

        Device findDevice = deviceRepository.findByUser(user).get();

        assertThat(device).isEqualTo(findDevice);
    }

    @Test
    void 사용자_디바이스_삭제() throws Exception {
        User user = User.builder()
            .nickname("Khl")
            .build();

        userRepository.save(user);

        Device device = Device.builder()
            .user(user)
            .deviceToken("1F46c3Hr")
            .fcmToken("gFFS190")
            .build();

        deviceRepository.save(device);

        em.clear();

        deviceRepository.deleteByUserId(user.getId());

        Optional<Device> findDevice = deviceRepository.findByUser(user);

        assertThat(findDevice).isEmpty();
    }

    @Test
    @Disabled
    void 만료된_fcm토큰_삭제() throws Exception {
        User user = User.builder()
            .nickname("Khl")
            .build();
        userRepository.save(user);

        Device device = Device.builder()
            .deviceToken("nSPKVZh")
            .fcmToken("z2G1")
            .user(user)
            .build();
        deviceRepository.save(device);

        RefreshToken refreshToken = RefreshToken.builder()
            .userId(user.getId())
            .build();
        refreshToken.setUpdatedAt(LocalDateTime.now().minusMonths(2).minusHours(2));
        refreshTokenRepository.save(refreshToken);

        em.flush();
        em.clear();

        deviceRepository.deleteExpiredFcmToken();
        Optional<Device> optionalDevice = deviceRepository.findById(device.getId());

        assertThat(optionalDevice).isEmpty();
    }
}