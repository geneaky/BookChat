package toy.bookchat.bookchat.security.jwt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

@SpringBootTest(classes = JwtTokenProvider.class)
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider tokenProvider;

    @Test
    public void 토큰을_생성한다() throws Exception {
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn()
        tokenProvider.createToken(authentication);
    }

}