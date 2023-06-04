package toy.bookchat.bookchat.exception.forbidden;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class ForbiddenException extends CustomException {

    public ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
