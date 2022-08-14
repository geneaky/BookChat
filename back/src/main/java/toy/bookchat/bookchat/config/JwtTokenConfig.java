package toy.bookchat.bookchat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("token")
public class JwtTokenConfig {

    private String secret;
    private long accessTokenExpiredTime;
    private long refreshTokenExpiredTime;


    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpiredTime() {
        return accessTokenExpiredTime;
    }

    public void setAccessTokenExpiredTime(long accessTokenExpiredTime) {
        this.accessTokenExpiredTime = accessTokenExpiredTime;
    }

    public long getRefreshTokenExpiredTime() {
        return refreshTokenExpiredTime;
    }

    public void setRefreshTokenExpiredTime(long refreshTokenExpiredTime) {
        this.refreshTokenExpiredTime = refreshTokenExpiredTime;
    }
}
