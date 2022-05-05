package toy.bookchat.bookchat.domain.book.service;

import toy.bookchat.bookchat.domain.book.dto.BookDto;

public interface BookSearchService {

    BookDto searchByIsbn(String isbn);

    BookDto searchByTitle(String title);

    BookDto searchByAuthor(String author);
}
