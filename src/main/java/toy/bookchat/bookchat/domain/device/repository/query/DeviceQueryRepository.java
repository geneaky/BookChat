package toy.bookchat.bookchat.domain.device.repository.query;

import java.util.List;
import toy.bookchat.bookchat.domain.device.DeviceEntity;

public interface DeviceQueryRepository {

    void deleteByUserId(Long userId);

    List<DeviceEntity> getDisconnectedUserDevice(Long roomId);

    void deleteExpiredFcmToken();
}
