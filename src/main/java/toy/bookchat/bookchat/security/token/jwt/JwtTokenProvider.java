package toy.bookchat.bookchat.security.token.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.response.Token;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String USER_NAME = "userName";
    private final String USER_ID = "userId";
    private final String EMAIL = "email";
    private final String USER_NICKNAME = "userNickname";
    private final String USER_PROFILE_IMAGE_URI = "userProfileImageUri";
    private final String DEFAULT_PROFILE_IMAGE_TYPE = "defaultProfileImageType";
    private final String USER_ROLE = "userRole";
    private final String SUB = "sub";
    private final String PROVIDER = "provider";

    private final JwtTokenProperties jwtTokenProperties;

    public JwtTokenProvider(JwtTokenProperties jwtTokenProperties) {
        this.jwtTokenProperties = jwtTokenProperties;
    }

    public Token createToken(User user) {
        return Token.builder()
            .accessToken(createAccessToken(user))
            .refreshToken(createRefreshToken(user))
            .build();
    }

    public String createRefreshToken(User user) {
        Date date = new Date();
        date.setTime(date.getTime() + jwtTokenProperties.getRefreshTokenExpiredTime());

        return Jwts.builder()
            .setClaims(createClaims(user))
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, jwtTokenProperties.getSecret())
            .compact();
    }

    public String createAccessToken(User user) {
        Date date = new Date();
        date.setTime(date.getTime() + jwtTokenProperties.getAccessTokenExpiredTime());

        return Jwts.builder()
            .setClaims(createClaims(user))
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, jwtTokenProperties.getSecret())
            .compact();
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SUB, "BookChat");
        claims.put(PROVIDER, user.getProvider());
        claims.put(USER_ID, user.getId().toString());
        claims.put(USER_NAME, user.getName());
        claims.put(USER_NICKNAME, user.getNickname());
        claims.put(EMAIL, user.getEmail());
        claims.put(USER_PROFILE_IMAGE_URI, user.getProfileImageUrl());
        claims.put(DEFAULT_PROFILE_IMAGE_TYPE, user.getDefaultProfileImageType());
        claims.put(USER_ROLE, user.getRoleName());
        return claims;
    }
}
