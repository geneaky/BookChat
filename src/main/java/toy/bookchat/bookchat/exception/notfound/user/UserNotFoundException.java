package toy.bookchat.bookchat.exception.notfound.user;

import static toy.bookchat.bookchat.exception.ErrorCode.USER_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super(USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }
}
