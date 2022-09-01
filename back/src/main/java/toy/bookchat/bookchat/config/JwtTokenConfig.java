package toy.bookchat.bookchat.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Getter
@ConstructorBinding
@ConfigurationProperties("token")
public class JwtTokenConfig {

    private final String secret;
    private final long accessTokenExpiredTime;
    private final long refreshTokenExpiredTime;

    public JwtTokenConfig(String secret, long accessTokenExpiredTime, long refreshTokenExpiredTime) {
        this.secret = secret;
        this.accessTokenExpiredTime = accessTokenExpiredTime;
        this.refreshTokenExpiredTime = refreshTokenExpiredTime;
    }
}
