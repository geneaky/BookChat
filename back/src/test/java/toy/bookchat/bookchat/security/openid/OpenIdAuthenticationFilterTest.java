package toy.bookchat.bookchat.security.openid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpenIdAuthenticationFilterTest {

    @Mock
    OpenIdTokenManager openIdTokenManager;
    @Mock
    UserRepository userRepository;
    @Mock
    IpBlockManager ipBlockManager;

    @InjectMocks
    OpenIdAuthenticationFilter openIdAuthenticationFilter;

    @Test
    public void 요청에_담긴_openid로_사용자_인증_성공() throws Exception {

    }
}