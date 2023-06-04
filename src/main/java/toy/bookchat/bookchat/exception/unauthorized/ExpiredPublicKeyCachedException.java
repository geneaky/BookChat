package toy.bookchat.bookchat.exception.unauthorized;

import static toy.bookchat.bookchat.exception.ErrorCode.EXPIRED_PUBLIC_KEY_CACHE;

public class ExpiredPublicKeyCachedException extends UnauthorizedException {

    public ExpiredPublicKeyCachedException() {
        super(EXPIRED_PUBLIC_KEY_CACHE, "인증 실패, 재시도 바랍니다.");
    }
}
