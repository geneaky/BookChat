package toy.bookchat.bookchat.domain.device.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.device.Device;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

  @Mock
  private DeviceReader deviceReader;
  @Mock
  private DeviceManager deviceManager;
  @InjectMocks
  private DeviceService deviceService;

  @Test
  void 디바이스_fcm_token_갱신() throws Exception {
    Device device = Device.builder()
        .fcmToken("old fcm token")
        .build();

    given(deviceReader.readUserDevice(any())).willReturn(device);

    deviceService.updateFcmToken(1L, "new fcm token");

    assertThat(device.getFcmToken()).isEqualTo("new fcm token");
    verify(deviceManager).update(any());
  }
}