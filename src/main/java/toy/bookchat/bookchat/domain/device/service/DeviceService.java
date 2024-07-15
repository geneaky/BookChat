package toy.bookchat.bookchat.domain.device.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.device.repository.DeviceRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
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
    public Optional<DeviceEntity> findUserDevice(UserEntity userEntity) {
        return deviceRepository.findByUserEntity(userEntity);
    }

    @Transactional
    public void registerDevice(DeviceEntity deviceEntity) {
        deviceRepository.save(deviceEntity);
    }

    @Transactional
    public void deleteUserDevice(Long userId) {
        deviceRepository.deleteByUserId(userId);
    }

    @Transactional
    public void updateFcmToken(Long userId, String fcmToken) {
        UserEntity userEntity = userReader.readUserEntity(userId);
        DeviceEntity deviceEntity = deviceReader.readUserDevice(userEntity);
        deviceEntity.changeFcmToken(fcmToken);
    }
}
