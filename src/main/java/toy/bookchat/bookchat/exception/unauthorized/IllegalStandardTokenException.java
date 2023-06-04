package toy.bookchat.bookchat.exception.unauthorized;

import static toy.bookchat.bookchat.exception.ErrorCode.ILLEGAL_STANDARD_TOKEN;

public class IllegalStandardTokenException extends UnauthorizedException {

    public IllegalStandardTokenException() {
        super(ILLEGAL_STANDARD_TOKEN, "올바른 토큰 구조가 아닙니다 ");
    }
}
