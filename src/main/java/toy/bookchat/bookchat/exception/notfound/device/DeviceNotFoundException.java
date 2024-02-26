package toy.bookchat.bookchat.exception.notfound.device;

import static toy.bookchat.bookchat.exception.ErrorCode.DEVICE_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class DeviceNotFoundException extends NotFoundException {

    public DeviceNotFoundException() {
        super(DEVICE_NOT_FOUND, "디바이스를 찾을 수 없습니다.");
    }
}
