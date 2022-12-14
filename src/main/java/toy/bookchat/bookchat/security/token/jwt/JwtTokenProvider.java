package toy.bookchat.bookchat.security.token.jwt;

import static toy.bookchat.bookchat.security.token.TokenConstants.DEFAULT_PROFILE_IMAGE_TYPE;
import static toy.bookchat.bookchat.security.token.TokenConstants.EMAIL;
import static toy.bookchat.bookchat.security.token.TokenConstants.PROVIDER;
import static toy.bookchat.bookchat.security.token.TokenConstants.SUB;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_ID;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_NAME;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_NICKNAME;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_PROFILE_IMAGE_URI;
import static toy.bookchat.bookchat.security.token.TokenConstants.USER_ROLE;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.JwtTokenProperties;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.api.dto.Token;

@Slf4j
@Component
public class JwtTokenProvider {

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
