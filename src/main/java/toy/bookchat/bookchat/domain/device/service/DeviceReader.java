package toy.bookchat.bookchat.domain.device.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.notfound.device.DeviceNotFoundException;

@Component
public class DeviceReader {

    private final DeviceRepository deviceRepository;

    public DeviceReader(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device readUserDevice(User user) {
        return deviceRepository.findByUser(user).orElseThrow(DeviceNotFoundException::new);
    }
}
