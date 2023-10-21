package toy.bookchat.bookchat.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;

@Component
public class FcmTokenScheduler {

    private final DeviceRepository deviceRepository;

    public FcmTokenScheduler(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void clearExpiredFcmTokens() {
        deviceRepository.deleteExpiredFcmToken();
    }
}
