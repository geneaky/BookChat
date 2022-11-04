package toy.bookchat.bookchat.domain.book.service;

import toy.bookchat.bookchat.domain.book.service.dto.request.BookSearchRequest;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;

public interface BookSearchService {

    BookSearchResponse searchByQuery(BookSearchRequest bookSearchRequest);
}
