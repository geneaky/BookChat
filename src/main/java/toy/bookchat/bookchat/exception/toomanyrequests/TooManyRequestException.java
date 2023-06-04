package toy.bookchat.bookchat.exception.toomanyrequests;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class TooManyRequestException extends CustomException {

    public TooManyRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
