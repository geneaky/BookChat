package toy.bookchat.bookchat.config.web;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "book")
public class BookSearchProperties {

    private final String header;
    private final String uri;

    public BookSearchProperties(String header, String uri) {
        this.header = header;
        this.uri = uri;
    }
}
