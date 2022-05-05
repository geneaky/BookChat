package toy.bookchat.bookchat.domain.book.service;

import toy.bookchat.bookchat.domain.book.dto.BookDto;

public interface BookSearchService {

    BookDto search(String isbn);
}
