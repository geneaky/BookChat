package toy.bookchat.bookchat.domain;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEARER;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;

import io.jsonwebtoken.Jwts;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;

@Testcontainers
public class StompTestExtension {

    @Container
    static RabbitMQContainer rabbitMQContainer;

    static {
        rabbitMQContainer = new RabbitMQContainer(
            "rabbitmq:3.11-management")
            .withPluginsEnabled("rabbitmq_stomp", "rabbitmq_web_stomp")
            .withUser("guest", "guest");
        rabbitMQContainer.setPortBindings(List.of("5672:5672", "15672:15672", "61613:61613"));
    }

    @MockBean
    JwtTokenManager jwtTokenManager;

    @BeforeEach
    public void setUp() {
        doReturn(getTokenPayload(getUser())).when(jwtTokenManager).getTokenPayloadFromToken(any());
    }

    protected User getUser() {
        return User.builder()
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .provider(OAuth2Provider.KAKAO)
            .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.ART))
            .build();
    }

    private TokenPayload getTokenPayload(User user) {
        return TokenPayload.of(user.getId(), user.getName(),
            user.getNickname(),
            user.getEmail(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            user.getRole());
    }

    protected String getTestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test");
        claims.put("name", "google123");
        claims.put("provider", GOOGLE);
        claims.put("email", "test@gmail.com");

        return BEARER + Jwts.builder()
            .setClaims(claims)
            .signWith(HS256, "test")
            .compact();
    }
}
