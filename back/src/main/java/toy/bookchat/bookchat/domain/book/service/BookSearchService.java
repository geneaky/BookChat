package toy.bookchat.bookchat.domain.book.service;

import java.util.List;
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;

public interface BookSearchService {

    List<BookDto> searchByIsbn(BookSearchRequestDto bookSearchRequestDto);

    List<BookDto> searchByTitle(BookSearchRequestDto bookSearchRequestDto);

    List<BookDto> searchByAuthor(BookSearchRequestDto bookSearchRequestDto);
}
