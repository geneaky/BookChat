package toy.bookchat.bookchat.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.device.repository.DeviceRepository;

@Component
public class FcmTokenScheduler {

    private final DeviceRepository deviceRepository;

    public FcmTokenScheduler(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void clearExpiredFcmTokens() {
        deviceRepository.deleteExpiredFcmToken();
    }
}
