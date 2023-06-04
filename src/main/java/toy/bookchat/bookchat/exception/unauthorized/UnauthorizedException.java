package toy.bookchat.bookchat.exception.unauthorized;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
