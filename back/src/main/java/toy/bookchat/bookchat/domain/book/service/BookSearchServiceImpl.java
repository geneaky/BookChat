package toy.bookchat.bookchat.domain.book.service;

import java.net.URI;
import java.util.List;
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
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.dto.KakaoBook;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String ISBN = "isbn";
    public static final String QUERY = "query";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    private final RestTemplate restTemplate;
    @Value("${book.api.uri}")
    private String apiUri;
    @Value("${book.api.header}")
    private String header;

    public BookSearchServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<BookDto> searchByIsbn(String isbn) {
        return getBookDtos(ISBN, isbn);
    }

    @Override
    public List<BookDto> searchByTitle(String title) {
        return getBookDtos(TITLE, title);
    }

    @Override
    public List<BookDto> searchByAuthor(String author) {
        return getBookDtos(AUTHOR, author);
    }

    private List<BookDto> getBookDtos(String queryOption, String queryParameter) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
            .fromUri(URI.create(apiUri + queryOption))
            .queryParam(QUERY, queryParameter);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTHORIZATION, header);
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        KakaoBook kakaoBook = restTemplate.exchange(uriComponentsBuilder.build().toUri(),
            HttpMethod.GET,
            httpEntity, KakaoBook.class).getBody();

        if (Optional.ofNullable(kakaoBook).isPresent()) {
            return kakaoBook.getBookDtos();
        }

        throw new BookNotFoundException("can't find book");
    }

}
