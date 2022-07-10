package toy.bookchat.bookchat.domain.book.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchResponseDto;
import toy.bookchat.bookchat.domain.book.dto.KakaoBook;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String ISBN = "isbn";
    public static final String QUERY = "query";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";

    public static final String SIZE = "size";

    public static final String PAGE = "page";

    public static final String SORT = "sort";
    private final RestTemplate restTemplate;
    @Value("${book.api.uri}")
    private String apiUri;
    @Value("${book.api.header}")
    private String header;

    public BookSearchServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public BookSearchResponseDto searchByIsbn(BookSearchRequestDto bookSearchRequestDto) {
        return fetchBooks(ISBN, bookSearchRequestDto);
    }

    @Override
    public BookSearchResponseDto searchByTitle(BookSearchRequestDto bookSearchRequestDto) {
        return fetchBooks(TITLE, bookSearchRequestDto);
    }

    @Override
    public BookSearchResponseDto searchByAuthor(BookSearchRequestDto bookSearchRequestDto) {
        return fetchBooks(AUTHOR, bookSearchRequestDto);
    }

    private BookSearchResponseDto fetchBooks(String queryOption,
        BookSearchRequestDto queryParameter) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + queryOption))
            .queryParam(QUERY, getQueryByQueryOption(queryOption, queryParameter))
            .queryParamIfPresent(PAGE, queryParameter.getPage())
            .queryParamIfPresent(SIZE, queryParameter.getSize())
            .queryParamIfPresent(SORT, queryParameter.getBookSearchSort());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTHORIZATION, header);
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        KakaoBook kakaoBook = restTemplate.exchange(uriComponentsBuilder.build().encode(
                StandardCharsets.UTF_8).toUri(),
            HttpMethod.GET,
            httpEntity, KakaoBook.class).getBody();

        if (Optional.ofNullable(kakaoBook).isPresent()) {
            return kakaoBook.getBookSearchResponseDto();
        }

        throw new BookNotFoundException("can't find book");
    }

    private String getQueryByQueryOption(String queryOption, BookSearchRequestDto queryParameter) {

        if (queryOption.equals(ISBN)) {
            return queryParameter.getIsbn();
        }

        if (queryOption.equals(TITLE)) {
            return queryParameter.getTitle();
        }

        if (queryOption.equals(AUTHOR)) {
            return queryParameter.getAuthor();
        }

        throw new IllegalArgumentException();
    }

}
