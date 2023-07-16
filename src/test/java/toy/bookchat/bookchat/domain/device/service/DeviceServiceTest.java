package toy.bookchat.bookchat.domain.device.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.user.User;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @InjectMocks
    private DeviceService deviceService;

    @Test
    void 디바이스_조회() throws Exception {
        User user = User.builder().build();
        deviceService.findUserDevice(user);

        verify(deviceRepository).findByUser(eq(user));
    }

    @Test
    void 디바이스_등록() throws Exception {
        Device device = Device.builder().build();
        deviceService.registerDevice(device);

        verify(deviceRepository).save(eq(device));
    }

    @Test
    void 디바이스_삭제() throws Exception {
        deviceService.deleteUserDevice(33L);

        verify(deviceRepository).deleteByUserId(eq(33L));
    }
}