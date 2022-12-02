package service;

import service.dto.request.BookSearchRequest;
import service.dto.response.BookSearchResponse;

public interface BookSearchService {

    BookSearchResponse searchByQuery(BookSearchRequest bookSearchRequest);
}
