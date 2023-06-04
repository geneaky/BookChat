package toy.bookchat.bookchat.exception.notfound.book;

import static toy.bookchat.bookchat.exception.ErrorCode.BOOK_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class BookNotFoundException extends NotFoundException {

    public BookNotFoundException() {
        super(BOOK_NOT_FOUND, "책을 찾을 수 없습니다.");
    }
}
