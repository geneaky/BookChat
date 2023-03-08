package toy.bookchat.bookchat.domain.book.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import toy.bookchat.bookchat.config.web.BookSearchProperties;
import toy.bookchat.bookchat.domain.book.service.dto.request.BookSearchRequest;
import toy.bookchat.bookchat.domain.book.service.dto.request.KakaoBook;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    public static final String QUERY = "query";
    public static final String SIZE = "size";
    public static final String PAGE = "page";
    public static final String SORT = "sort";
    private final BookFetcher<KakaoBook> bookFetcher;
    private final BookSearchProperties bookSearchProperties;

    public BookSearchServiceImpl(BookFetcher<KakaoBook> bookFetcher,
        BookSearchProperties bookSearchProperties) {
        this.bookFetcher = bookFetcher;
        this.bookSearchProperties = bookSearchProperties;
    }

    @Override
    public BookSearchResponse searchByQuery(BookSearchRequest bookSearchRequest) {
        return bookFetcher.fetchBooks(createUri(bookSearchRequest),
                httpHeaders -> httpHeaders.setAll(createHeader()))
            .getBookSearchResponse();
    }

    private Map<String, String> createHeader() {
        Map<String, String> headerMap = new HashMap<>(3);
        headerMap.put(AUTHORIZATION, bookSearchProperties.getHeader());
        headerMap.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headerMap;
    }

    private URI createUri(BookSearchRequest bookSearchRequest) {
        return UriComponentsBuilder
            .fromUri(URI.create(bookSearchProperties.getUri()))
            .queryParam(QUERY, bookSearchRequest.getQuery())
            .queryParamIfPresent(PAGE, bookSearchRequest.getPage())
            .queryParamIfPresent(SIZE, bookSearchRequest.getSize())
            .queryParamIfPresent(SORT, bookSearchRequest.getSort())
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUri();
    }
}
