package toy.bookchat.bookchat.domain.device.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.user.User;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }


    @Transactional(readOnly = true)
    public Optional<Device> findUserDevice(User user) {
        return deviceRepository.findByUser(user);
    }

    @Transactional
    public void registerDevice(Device device) {
        deviceRepository.save(device);
    }

    @Transactional
    public void deleteUserDevice(Long userId) {
        deviceRepository.deleteByUserId(userId);
    }
}
