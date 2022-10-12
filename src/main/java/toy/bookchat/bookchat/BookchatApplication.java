package toy.bookchat.bookchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.config.OAuth2Config;
import toy.bookchat.bookchat.config.aws.S3Config;

@SpringBootApplication
@EnableConfigurationProperties({JwtTokenConfig.class, OAuth2Config.class, S3Config.class})
public class BookchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookchatApplication.class, args);
    }

}
