package toy.bookchat.bookchat.domain.book.service;

import toy.bookchat.bookchat.domain.book.dto.request.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.response.BookSearchResponseDto;

public interface BookSearchService {

    BookSearchResponseDto searchByQuery(BookSearchRequestDto bookSearchRequestDto);
}
