package toy.bookchat.bookchat.exception.conflict;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class ConflictException extends CustomException {

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
