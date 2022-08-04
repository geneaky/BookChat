package toy.bookchat.bookchat.security.jwt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@ExtendWith(MockitoExtension.class)
@Slf4j
class JwtTokenProviderTest {

    JwtTokenProvider tokenProvider = new JwtTokenProvider();

    @Test
    public void 토큰을_생성한다() throws Exception {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        Map<String, Object> oAuth2Provider = new HashMap<>();
        oAuth2Provider.put("social_type", OAuth2Provider.kakao);
        Map<String, Object> map = new HashMap<>();
        map.put("email", "kaktus418@gmail.com");
        oAuth2Provider.put("kakao_account", map);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getAttributes()).thenReturn(oAuth2Provider);

        String token = tokenProvider.createToken(authentication);
        log.info(token);
    }

}