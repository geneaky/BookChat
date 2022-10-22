package toy.bookchat.bookchat.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

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
    UserRepository userRepository;
    @MockBean
    IpBlockManager ipBlockManager;

    private User getUser() {
        return User.builder()
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

    private UserPrincipal getUserPrincipal() {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        User user = getUser();

        return new UserPrincipal(1L, user.getEmail(), user.getName(), user.getNickname(),
            user.getProfileImageUrl(),
            user.getDefaultProfileImageType(), authorities, user);
    }

    @BeforeEach
    public void setUp() {
        when(userRepository.findByName(any())).thenReturn(Optional.ofNullable(getUser()));
        when(ipBlockManager.validateRequest(any())).thenReturn(true);
    }

    public OpenIdTokenConfig getOpenIdTokenConfig() {
        return this.openIdTokenConfig;
    }

    public OpenIdTokenManager getOpenIdTokenManager() {
        return this.openIdTokenManager;
    }
}
