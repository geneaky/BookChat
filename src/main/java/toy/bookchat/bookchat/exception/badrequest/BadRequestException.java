package toy.bookchat.bookchat.exception.badrequest;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class BadRequestException extends CustomException {

    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
