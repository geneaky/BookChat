package toy.bookchat.bookchat.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Config {

    private final String kakaoURI;
    private final String googleURI;

    public OAuth2Config(String kakaoURI, String googleURI) {
        this.kakaoURI = kakaoURI;
        this.googleURI = googleURI;
    }
}
