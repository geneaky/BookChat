package toy.bookchat.bookchat.security.openid;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

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
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        User user = User.builder()
            .name("352kakao")
            .provider(OAuth2Provider.KAKAO)
            .email("test@gmail.com")
            .nickName("testnick")
            .role(ROLE.USER)
            .build();

        String bearerToken = "Bearer 90YJ4D3";

        when(httpServletRequest.getHeader(any())).thenReturn(bearerToken);
        when(userRepository.findByName(any())).thenReturn(Optional.of(user));

        openIdAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse,
            filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    public void 유효하지않은_openid로_요청시_예외발생() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Assertions.assertThatThrownBy(() -> {
            openIdAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse,
                filterChain);
        }).isInstanceOf(DenidedTokenException.class);
    }

    /* TODO: 2022-09-08 양방향 rsa256 utill class를 만들어서 openidconnect를 테스트 / 블로깅
     */


}