package toy.bookchat.bookchat.config.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {

    private final String kakaoUri;
    private final String googleUri;

    public OAuth2Properties(String kakaoUri, String googleUri) {
        this.kakaoUri = kakaoUri;
        this.googleUri = googleUri;
    }
}
