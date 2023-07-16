package toy.bookchat.bookchat.exception.conflict.device;

import static toy.bookchat.bookchat.exception.ErrorCode.DEVICE_USAGE_CONFLICT;

import toy.bookchat.bookchat.exception.conflict.ConflictException;

public class DeviceAlreadyRegisteredException extends ConflictException {

    public DeviceAlreadyRegisteredException() {
        super(DEVICE_USAGE_CONFLICT, "다른 기기에서 로그인되어 있습니다.");
    }
}
