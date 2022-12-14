package toy.bookchat.bookchat.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "token")
public class JwtTokenProperties {

    private final String secret;
    private final long accessTokenExpiredTime;
    private final long refreshTokenExpiredTime;
    private final long reissuePeriod;

    public JwtTokenProperties(String secret, long accessTokenExpiredTime,
        long refreshTokenExpiredTime,
        long reissuePeriod) {
        this.secret = secret;
        this.accessTokenExpiredTime = accessTokenExpiredTime;
        this.refreshTokenExpiredTime = refreshTokenExpiredTime;
        this.reissuePeriod = reissuePeriod;
    }
}
