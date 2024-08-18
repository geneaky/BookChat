package toy.bookchat.bookchat.domain.device.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.exception.notfound.device.DeviceNotFoundException;

@Component
public class DeviceManager {

  private final DeviceRepository deviceRepository;

  public DeviceManager(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  public void update(Device device) {
    DeviceEntity deviceEntity = deviceRepository.findById(device.getId()).orElseThrow(DeviceNotFoundException::new);

    deviceEntity.changeFcmToken(device.getFcmToken());
  }
}
