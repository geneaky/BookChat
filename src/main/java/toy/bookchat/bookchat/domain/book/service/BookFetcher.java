package toy.bookchat.bookchat.domain.book.service;

import java.net.URI;
import java.util.function.Consumer;
import org.springframework.http.HttpHeaders;

public interface BookFetcher<T> {

    T fetchBooks(URI uri, Consumer<HttpHeaders> headersConsumer);
}
