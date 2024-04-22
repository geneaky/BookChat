package toy.bookchat.bookchat.exception.notfound.scrap;

import static toy.bookchat.bookchat.exception.ErrorCode.SCRAP_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class ScrapNotFoundException extends NotFoundException {

    public ScrapNotFoundException() {
        super(SCRAP_NOT_FOUND, "스크랩을 찾을 수 없습니다.");
    }
}
