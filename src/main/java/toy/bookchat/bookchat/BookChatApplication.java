package toy.bookchat.bookchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.config.web.BookSearchProperties;
import toy.bookchat.bookchat.config.websocket.ExternalBrokerProperties;
import toy.bookchat.bookchat.infrastructure.s3.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtTokenProperties.class, OAuth2Properties.class,
    StorageProperties.class, ExternalBrokerProperties.class, BookSearchProperties.class})
public class BookChatApplication {

  public static void main(String[] args) {
    SpringApplication.run(BookChatApplication.class, args);
  }
}
