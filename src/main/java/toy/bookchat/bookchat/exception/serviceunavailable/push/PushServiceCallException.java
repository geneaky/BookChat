package toy.bookchat.bookchat.exception.serviceunavailable.push;

import static toy.bookchat.bookchat.exception.ErrorCode.PUSH_SERVICE_FAIL;

import toy.bookchat.bookchat.exception.serviceunavailable.ServiceUnavailableException;

public class PushServiceCallException extends ServiceUnavailableException {

    public PushServiceCallException() {
        super(PUSH_SERVICE_FAIL, "Push 발송중 예외가 발생하였습니다. 다시 시도해주세요.");
    }
}
