package toy.bookchat.bookchat.localtest;

import static toy.bookchat.bookchat.support.Status.ACTIVE;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;

@Primary
@Profile("local")
@Component
public class LocalTestConfig implements JwtTokenManager {

  private final UserRepository userRepository;
  private final Flyway flyway;
  private UserEntity userEntity;

  public LocalTestConfig(UserRepository userRepository, Flyway flyway) {
    this.userRepository = userRepository;
    this.flyway = flyway;
  }

  @PostConstruct
  public void init() {
    flyway.clean();
    flyway.migrate();
    userEntity = UserEntity.builder()
        .name("google123")
        .nickname("geneaky")
        .email("kaktus41@gmail.com")
        .defaultProfileImageType(1)
        .profileImageUrl(null)
        .provider(OAuth2Provider.GOOGLE)
        .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.HEALTH))
        .role(ROLE.USER)
        .status(ACTIVE)
        .build();
    userRepository.save(userEntity);
  }

  @PreDestroy
  public void finalize() {
    userRepository.deleteAll();
  }

  @Override
  public String extractTokenFromAuthorizationHeader(String header) {
    return null;
  }

  @Override
  public Long getUserIdFromToken(String token) {
    return this.userEntity.getId();
  }

  @Override
  public String getOAuth2MemberNumberFromToken(String token) {
    return this.userEntity.getName();
  }

  @Override
  public String getUserEmailFromToken(String token) {
    return this.userEntity.getEmail();
  }

  @Override
  public boolean shouldRefreshTokenBeRenew(String token) {
    return false;
  }

  @Override
  public TokenPayload getTokenPayloadFromToken(String token) {
    return TokenPayload.of(userEntity.getId(), userEntity.getName(), userEntity.getNickname(), userEntity.getEmail(),
        userEntity.getProfileImageUrl(), userEntity.getDefaultProfileImageType(), userEntity.getRole());
  }
}
