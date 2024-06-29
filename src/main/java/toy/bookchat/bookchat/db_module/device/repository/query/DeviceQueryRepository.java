package toy.bookchat.bookchat.db_module.device.repository.query;

import java.util.List;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;

public interface DeviceQueryRepository {

    void deleteByUserId(Long userId);

    List<DeviceEntity> getDisconnectedUserDevice(Long roomId);

    void deleteExpiredFcmToken();
}
