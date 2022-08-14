package toy.bookchat.bookchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import toy.bookchat.bookchat.config.JwtTokenConfig;

@SpringBootApplication
@EnableConfigurationProperties(JwtTokenConfig.class)
public class BookchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookchatApplication.class, args);
    }

}
