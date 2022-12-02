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
import toy.bookchat.bookchat.domain.book.service.dto.request.BookSearchRequest;
import toy.bookchat.bookchat.domain.book.service.dto.request.KakaoBook;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String QUERY = "query";
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
    public BookSearchResponse searchByQuery(BookSearchRequest bookSearchRequest) {
        return fetchBooks(bookSearchRequest);
    }


    private BookSearchResponse fetchBooks(BookSearchRequest bookSearchRequest) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri))
            .queryParam(QUERY, bookSearchRequest.getQuery())
            .queryParamIfPresent(PAGE, bookSearchRequest.getPage())
            .queryParamIfPresent(SIZE, bookSearchRequest.getSize())
            .queryParamIfPresent(SORT, bookSearchRequest.getSort());

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
            return kakaoBook.getBookSearchResponse();
        }

        throw new BookNotFoundException();
    }
}
