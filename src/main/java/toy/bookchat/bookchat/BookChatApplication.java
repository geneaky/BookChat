package toy.bookchat.bookchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import toy.bookchat.bookchat.config.aws.StorageProperties;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.config.token.OAuth2Properties;
import toy.bookchat.bookchat.config.websocket.ExternalBrokerProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtTokenProperties.class, OAuth2Properties.class,
    StorageProperties.class, ExternalBrokerProperties.class})
public class BookChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookChatApplication.class, args);
    }
}
