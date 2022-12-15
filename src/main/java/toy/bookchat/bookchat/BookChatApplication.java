package toy.bookchat.bookchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import toy.bookchat.bookchat.config.aws.StorageProperties;
import toy.bookchat.bookchat.config.security.JwtTokenProperties;
import toy.bookchat.bookchat.config.security.OAuth2Properties;

@SpringBootApplication
@EnableConfigurationProperties({JwtTokenProperties.class, OAuth2Properties.class,
    StorageProperties.class})
public class BookChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookChatApplication.class, args);
    }

}
