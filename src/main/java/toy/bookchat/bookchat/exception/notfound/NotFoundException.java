package toy.bookchat.bookchat.exception.notfound;

import toy.bookchat.bookchat.exception.CustomException;
import toy.bookchat.bookchat.exception.ErrorCode;

public class NotFoundException extends CustomException {

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
