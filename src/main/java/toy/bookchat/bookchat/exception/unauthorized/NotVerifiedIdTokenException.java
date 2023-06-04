package toy.bookchat.bookchat.exception.unauthorized;

import static toy.bookchat.bookchat.exception.ErrorCode.NOT_VERIFIED_TOKEN;

public class NotVerifiedIdTokenException extends UnauthorizedException {

    public NotVerifiedIdTokenException() {
        super(NOT_VERIFIED_TOKEN, "유효하지 않은 ID 토큰입니다.");
    }
}
