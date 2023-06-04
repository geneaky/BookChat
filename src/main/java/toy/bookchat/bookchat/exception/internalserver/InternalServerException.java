package toy.bookchat.bookchat.exception.internalserver;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class InternalServerException extends CustomException {

    public InternalServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
