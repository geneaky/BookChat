package toy.bookchat.bookchat.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.handler.RestAuthenticationEntryPoint;
import toy.bookchat.bookchat.security.ipblock.IpBlockCheckingFilter;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.openid.OpenIdAuthenticationFilter;
import toy.bookchat.bookchat.security.openid.OpenIdTokenManager;

public abstract class AuthenticationTestExtension {

    @MockBean
    IpBlockManager ipBlockManager;
    @MockBean
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        when(ipBlockManager.validateRequest(any())).thenReturn(true);
    }

}
