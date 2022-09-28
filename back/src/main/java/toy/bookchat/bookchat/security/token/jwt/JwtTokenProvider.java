package toy.bookchat.bookchat.security.token.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenConfig;
import toy.bookchat.bookchat.domain.user.api.dto.Token;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Slf4j
@Component
public class JwtTokenProvider {

    private final JwtTokenConfig jwtTokenConfig;

    public JwtTokenProvider(JwtTokenConfig jwtTokenConfig) {
        this.jwtTokenConfig = jwtTokenConfig;
    }

    public Token createToken(String userName, String userEmail, OAuth2Provider oAuth2Provider) {
        return Token.builder()
            .accessToken(createAccessToken(userName, userEmail, oAuth2Provider))
            .refreshToken(createRefreshToken(userName, userEmail, oAuth2Provider))
            .build();
    }

    private String createRefreshToken(String userName, String userEmail,
        OAuth2Provider oAuth2Provider) {
        Map<String, Object> claims = createClaims(userName, userEmail, oAuth2Provider);

        Date date = new Date();
        date.setTime(date.getTime() + jwtTokenConfig.getRefreshTokenExpiredTime());

        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, jwtTokenConfig.getSecret())
            .compact();
    }

    private String createAccessToken(String userName, String userEmail,
        OAuth2Provider oAuth2Provider) {
        Map<String, Object> claims = createClaims(userName, userEmail, oAuth2Provider);

        Date date = new Date();
        date.setTime(date.getTime() + jwtTokenConfig.getAccessTokenExpiredTime());

        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, jwtTokenConfig.getSecret())
            .compact();
    }

    private Map<String, Object> createClaims(String userName, String userEmail,
        OAuth2Provider oAuth2Provider) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "BookChat");
        claims.put("oAuth2Provider", oAuth2Provider);
        claims.put("userName", userName);
        claims.put("email", userEmail);
        return claims;
    }
}
