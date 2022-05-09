package toy.bookchat.bookchat.domain.book.service;

import java.util.List;
import toy.bookchat.bookchat.domain.book.dto.BookDto;

public interface BookSearchService {

    List<BookDto> searchByIsbn(String isbn);

    List<BookDto> searchByTitle(String title);

    List<BookDto> searchByAuthor(String author);
}
