package toy.bookchat.bookchat.domain.device.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@RepositoryTest
class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;
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
}