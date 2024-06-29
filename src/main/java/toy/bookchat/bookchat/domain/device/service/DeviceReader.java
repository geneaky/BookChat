package toy.bookchat.bookchat.domain.device.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.device.repository.DeviceRepository;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.exception.notfound.device.DeviceNotFoundException;

@Component
public class DeviceReader {

    private final DeviceRepository deviceRepository;

    public DeviceReader(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceEntity readUserDevice(UserEntity userEntity) {
        return deviceRepository.findByUserEntity(userEntity).orElseThrow(DeviceNotFoundException::new);
    }
}
