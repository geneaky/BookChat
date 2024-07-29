package toy.bookchat.bookchat.security.token.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.config.token.JwtTokenProperties;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.user.api.v1.response.Token;

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

  public Token createToken(UserEntity userEntity) {
    return Token.builder()
        .accessToken(createAccessToken(userEntity))
        .refreshToken(createRefreshToken(userEntity))
        .build();
  }

  public String createRefreshToken(UserEntity userEntity) {
    Date date = new Date();
    date.setTime(date.getTime() + jwtTokenProperties.getRefreshTokenExpiredTime());

    return Jwts.builder()
        .setClaims(createClaims(userEntity))
        .setExpiration(date)
        .signWith(SignatureAlgorithm.HS256, jwtTokenProperties.getSecret())
        .compact();
  }

  public String createAccessToken(UserEntity userEntity) {
    Date date = new Date();
    date.setTime(date.getTime() + jwtTokenProperties.getAccessTokenExpiredTime());

    return Jwts.builder()
        .setClaims(createClaims(userEntity))
        .setExpiration(date)
        .signWith(SignatureAlgorithm.HS256, jwtTokenProperties.getSecret())
        .compact();
  }

  private Map<String, Object> createClaims(UserEntity userEntity) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(SUB, "BookChat");
    claims.put(PROVIDER, userEntity.getProvider());
    claims.put(USER_ID, userEntity.getId().toString());
    claims.put(USER_NAME, userEntity.getName());
    claims.put(USER_NICKNAME, userEntity.getNickname());
    claims.put(EMAIL, userEntity.getEmail());
    claims.put(USER_PROFILE_IMAGE_URI, userEntity.getProfileImageUrl());
    claims.put(DEFAULT_PROFILE_IMAGE_TYPE, userEntity.getDefaultProfileImageType());
    claims.put(USER_ROLE, userEntity.getRoleName());
    return claims;
  }
}
