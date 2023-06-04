package toy.bookchat.bookchat.exception.unauthorized;

import static toy.bookchat.bookchat.exception.ErrorCode.EXPIRED_TOKEN;

public class ExpiredTokenException extends UnauthorizedException {

    public ExpiredTokenException() {
        super(EXPIRED_TOKEN, "만료된 토큰입니다.");
    }
}
