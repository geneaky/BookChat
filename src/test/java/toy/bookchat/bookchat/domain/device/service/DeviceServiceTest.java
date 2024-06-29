package toy.bookchat.bookchat.domain.device.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.device.DeviceEntity;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.user.UserEntity;
import toy.bookchat.bookchat.domain.user.service.UserReader;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DeviceReader deviceReader;
    @Mock
    private UserReader userReader;
    @InjectMocks
    private DeviceService deviceService;

    @Test
    void 디바이스_조회() throws Exception {
        UserEntity userEntity = UserEntity.builder().build();
        deviceService.findUserDevice(userEntity);

        verify(deviceRepository).findByUserEntity(eq(userEntity));
    }

    @Test
    void 디바이스_등록() throws Exception {
        DeviceEntity deviceEntity = DeviceEntity.builder().build();
        deviceService.registerDevice(deviceEntity);

        verify(deviceRepository).save(eq(deviceEntity));
    }

    @Test
    void 디바이스_삭제() throws Exception {
        deviceService.deleteUserDevice(33L);

        verify(deviceRepository).deleteByUserId(eq(33L));
    }

    @Test
    void 디바이스_fcm_token_갱신() throws Exception {
        DeviceEntity deviceEntity = DeviceEntity.builder()
            .fcmToken("old fcm token")
            .build();

        given(deviceReader.readUserDevice(any())).willReturn(deviceEntity);

        deviceService.updateFcmToken(1L, "new fcm token");

        assertThat(deviceEntity.getFcmToken()).isEqualTo("new fcm token");
    }
}