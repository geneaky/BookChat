package toy.bookchat.bookchat.exception.unauthorized;

import static toy.bookchat.bookchat.exception.ErrorCode.DENIED_TOKEN;

public class DeniedTokenException extends UnauthorizedException {

    public DeniedTokenException() {
        super(DENIED_TOKEN, "토큰이 거부되었습니다.");
    }
}
