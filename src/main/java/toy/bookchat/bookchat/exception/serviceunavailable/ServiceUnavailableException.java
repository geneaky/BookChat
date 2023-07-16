package toy.bookchat.bookchat.exception.serviceunavailable;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class ServiceUnavailableException extends CustomException {

    public ServiceUnavailableException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
