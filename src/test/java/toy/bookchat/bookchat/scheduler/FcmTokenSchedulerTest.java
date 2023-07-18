package toy.bookchat.bookchat.scheduler;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;

@ExtendWith(MockitoExtension.class)
class FcmTokenSchedulerTest {

    @Mock
    private DeviceRepository deviceRepository;
    @InjectMocks
    private FcmTokenScheduler fcmTokenScheduler;

    @Test
    void 리프레시토큰의_update시간에_1달2주를_더한_시간이_현재보다_과거일경우_삭제한다() throws Exception {
        fcmTokenScheduler.clearExpiredFcmTokens();

        verify(deviceRepository).deleteExpiredFcmToken();
    }
}