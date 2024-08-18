package toy.bookchat.bookchat.domain.device.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.device.Device;

@Service
public class DeviceService {

  private final DeviceReader deviceReader;
  private final DeviceManager deviceManager;

  public DeviceService(DeviceReader deviceReader, DeviceManager deviceManager) {
    this.deviceReader = deviceReader;
    this.deviceManager = deviceManager;
  }

  @Transactional
  public void updateFcmToken(Long userId, String fcmToken) {
    Device device = deviceReader.readUserDevice(userId);
    device.changeFcmToken(fcmToken);
    deviceManager.update(device);
  }
}
