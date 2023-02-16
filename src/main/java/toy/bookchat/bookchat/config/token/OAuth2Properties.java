package toy.bookchat.bookchat.config.token;

import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {

    private final String kakaoUri;
    private final String kakaoAppKey;
    private final List<String> googleClientIds;

    public OAuth2Properties(String kakaoUri, String kakaoAppKey, List<String> googleClientIds) {
        this.kakaoUri = kakaoUri;
        this.kakaoAppKey = kakaoAppKey;
        this.googleClientIds = googleClientIds;
    }
}
