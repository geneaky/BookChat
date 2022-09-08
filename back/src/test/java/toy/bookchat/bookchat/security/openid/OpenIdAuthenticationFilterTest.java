package toy.bookchat.bookchat.security.openid;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        openIdAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(filterChain).doFilter(any(), any());
    }
    
    @Test
    public void 유효하지않은_openid로_요청시_예외발생() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(openIdTokenManager.getOauth2MemberNumberFromRequest(any())).thenThrow(ExpiredTokenException.class);

        Assertions.assertThatThrownBy(() -> {
            openIdAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        }).isInstanceOf(ExpiredTokenException.class);
    }

    /* TODO: 2022-09-08 양방향 rsa256 util class를 만들어서 openidconnect를 테스트
     */
}