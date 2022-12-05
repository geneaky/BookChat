package toy.bookchat.bookchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.config.OAuth2Config;
import toy.bookchat.bookchat.config.aws.S3Config;

@SpringBootApplication
@EnableConfigurationProperties({JwtTokenConfig.class, OAuth2Config.class, S3Config.class})
public class BookChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookChatApplication.class, args);
    }

}
