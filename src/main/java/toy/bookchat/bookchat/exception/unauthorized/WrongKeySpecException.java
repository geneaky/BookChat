package toy.bookchat.bookchat.exception.unauthorized;

import static toy.bookchat.bookchat.exception.ErrorCode.WRONG_KEY_SPEC;

public class WrongKeySpecException extends UnauthorizedException {

    public WrongKeySpecException() {
        super(WRONG_KEY_SPEC, "잘못된 키 생성 형식입니다.");
    }
}
