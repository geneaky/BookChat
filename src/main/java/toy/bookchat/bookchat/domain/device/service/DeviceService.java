package toy.bookchat.bookchat.domain.device.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserReader userReader;
    private final DeviceReader deviceReader;

    public DeviceService(DeviceRepository deviceRepository, UserReader userReader, DeviceReader deviceReader) {
        this.deviceRepository = deviceRepository;
        this.userReader = userReader;
        this.deviceReader = deviceReader;
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

    @Transactional
    public void updateFcmToken(Long userId, String fcmToken) {
        User user = userReader.readUser(userId);
        Device device = deviceReader.readUserDevice(user);
        device.changeFcmToken(fcmToken);
    }
}
