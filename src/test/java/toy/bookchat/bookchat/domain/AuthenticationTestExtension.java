package toy.bookchat.bookchat.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;

/*
    controller 테스트는 security까지 포함시켜 테스트하여 restdoc 문서에
    token, provider_type과 같은 정보가 포함되도록 진행
 */
public abstract class AuthenticationTestExtension {

    @MockBean
    OpenIdTokenManager openIdTokenManager;
    @MockBean
    JwtTokenManager jwtTokenManager;
    @MockBean
    OpenIdTokenConfig openIdTokenConfig;
    @MockBean
    IpBlockManager ipBlockManager;

    private User getUser() {
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

    @BeforeEach
    public void setUp() {
        when(jwtTokenManager.getTokenPayloadFromToken(any())).thenReturn(
            getTokenPayload(getUser()));
        when(ipBlockManager.validateRequest(any())).thenReturn(true);
    }

    public OpenIdTokenConfig getOpenIdTokenConfig() {
        return this.openIdTokenConfig;
    }

    public OpenIdTokenManager getOpenIdTokenManager() {
        return this.openIdTokenManager;
    }
}
