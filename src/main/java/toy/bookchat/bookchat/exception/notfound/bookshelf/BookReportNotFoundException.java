package toy.bookchat.bookchat.exception.notfound.bookshelf;

import static toy.bookchat.bookchat.exception.ErrorCode.BOOK_REPORT_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class BookReportNotFoundException extends NotFoundException {

    public BookReportNotFoundException() {
        super(BOOK_REPORT_NOT_FOUND, "독후감을 찾을 수 없습니다.");
    }
}
