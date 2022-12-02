package service;


import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import exception.BookNotFoundException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import service.dto.request.BookSearchRequest;
import service.dto.request.KakaoBook;
import service.dto.response.BookSearchResponse;


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
        httpHeaders.set(ACCEPT, APPLICATION_JSON_VALUE);
        httpHeaders.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

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
