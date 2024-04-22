package toy.bookchat.bookchat.exception.notfound.agony;

import static toy.bookchat.bookchat.exception.ErrorCode.AGONY_RECORD_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class AgonyRecordNotFoundException extends NotFoundException {

    public AgonyRecordNotFoundException() {
        super(AGONY_RECORD_NOT_FOUND, "고민 기록을 찾을 수 없습니다.");
    }
}
