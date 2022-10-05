package toy.bookchat.bookchat.domain.book.service;

import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchResponseDto;

public interface BookSearchService {
    BookSearchResponseDto searchByQuery(BookSearchRequestDto bookSearchRequestDto);
}
