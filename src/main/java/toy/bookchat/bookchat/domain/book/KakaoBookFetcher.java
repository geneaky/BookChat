package toy.bookchat.bookchat.domain.book;

import java.net.URI;
import java.util.function.Consumer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import toy.bookchat.bookchat.domain.book.service.BookFetcher;
import toy.bookchat.bookchat.domain.book.service.dto.request.KakaoBook;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@Component
public class KakaoBookFetcher implements BookFetcher<KakaoBook> {

    private final WebClient webClient;

    public KakaoBookFetcher(WebClient webClient) {
        this.webClient = webClient;
    }

    public KakaoBook fetchBooks(URI uri, Consumer<HttpHeaders> headersConsumer) {
        KakaoBook kakaoBook = webClient.get().uri(uri)
            .headers(headersConsumer)
            .retrieve()
            .bodyToMono(KakaoBook.class).block();

        checkKakaoBookIsExisted(kakaoBook);

        return kakaoBook;
    }

    private void checkKakaoBookIsExisted(KakaoBook kakaoBook) {
        if (kakaoBook == null) {
            throw new BookNotFoundException();
        }
    }
}
