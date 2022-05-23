package toy.bookchat.bookchat.domain.bookshelf.service;

import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;

public interface BookShelfService {

    void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto);
}
