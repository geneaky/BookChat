package toy.bookchat.bookchat.domain.device.repository.query;

import java.util.List;
import toy.bookchat.bookchat.domain.device.Device;

public interface DeviceQueryRepository {

    void deleteByUserId(Long userId);

    List<Device> getDisconnectedUserDevice(Long roomId);
}
