package toy.bookchat.bookchat.exception.notfound.agony;

import static toy.bookchat.bookchat.exception.ErrorCode.AGONY_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class AgonyNotFoundException extends NotFoundException {

    public AgonyNotFoundException() {
        super(AGONY_NOT_FOUND, "고민을 찾을 수 없습니다.");
    }
}
