package toy.bookchat.bookchat.exception.badrequest.user;

import static toy.bookchat.bookchat.exception.ErrorCode.USER_ALREADY_SIGN_UP;

import toy.bookchat.bookchat.exception.badrequest.BadRequestException;

public class UserAlreadySignUpException extends BadRequestException {

    public UserAlreadySignUpException() {
        super(USER_ALREADY_SIGN_UP, "이미 가입된 사용자입니다.");
    }
}
