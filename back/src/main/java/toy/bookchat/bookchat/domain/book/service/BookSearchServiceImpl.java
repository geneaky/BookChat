package toy.bookchat.bookchat.domain.book.service;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.dto.KakaoBook;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    private final RestTemplate restTemplate;
    @Value("${book.api.uri}")
    private String apiUri;
    @Value("${book.api.header}")
    private String header;

    public BookSearchServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public BookDto searchByIsbn(String isbn) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + "isbn"))
            .queryParam("query", isbn);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", header);
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        KakaoBook kakaoBook = restTemplate.exchange(uriComponentsBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity, KakaoBook.class).getBody();

        return kakaoBook.getBookDto().get(0);
    }

    @Override
    public BookDto searchByTitle(String title) {
        return null;
    }

    @Override
    public BookDto searchByAuthor(String author) {
        return null;
    }
}
