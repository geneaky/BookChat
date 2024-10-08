package toy.bookchat.bookchat.domain.device.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;
import toy.bookchat.bookchat.db_module.device.repository.DeviceRepository;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.exception.notfound.device.DeviceNotFoundException;

@Component
public class DeviceReader {

  private final DeviceRepository deviceRepository;

  public DeviceReader(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  public Device readUserDevice(Long userId) {
    DeviceEntity deviceEntity = deviceRepository.findByUserId(userId).orElseThrow(DeviceNotFoundException::new);

    return Device.builder()
        .id(deviceEntity.getId())
        .fcmToken(deviceEntity.getFcmToken())
        .build();
  }

  public List<Device> readDisconnectedUserDevice(Long roomId) {
    List<DeviceEntity> deviceEntities = deviceRepository.getDisconnectedUserDevice(roomId);

    return deviceEntities.stream()
        .map(
            deviceEntity -> Device.builder()
                .id(deviceEntity.getId())
                .fcmToken(deviceEntity.getFcmToken())
                .build()
        )
        .collect(Collectors.toList());
  }
}

