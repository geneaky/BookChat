package toy.bookchat.bookchat.security.openid;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.AUTHORIZATION;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.PROVIDER_TYPE;

import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.NotVerifiedRequestFormatException;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.openid.OpenIdAuthenticationFilter;
import toy.bookchat.bookchat.security.token.openid.OpenIdTokenManager;

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
    public void 요청에_openid_provider가_없을시_예외발생() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        String randomBearerToken = "Bearer tLA7p";
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(randomBearerToken);
        when(httpServletRequest.getHeader("provider_type")).thenReturn(null);

        assertThatThrownBy(() -> {
            openIdAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse,
                filterChain);
        }).isInstanceOf(NotVerifiedRequestFormatException.class);
    }

    @Test
    public void 요청에_담긴_openid와_openid_provider로_사용자_인증_성공() throws Exception {
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

        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(bearerToken);
        when(httpServletRequest.getHeader(PROVIDER_TYPE)).thenReturn("KAKAO");
        when(userRepository.findByName(any())).thenReturn(Optional.of(user));

        openIdAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse,
            filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    public void openid없이_요청시_예외발생() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);

        assertThatThrownBy(() -> {
            openIdAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse,
                filterChain);
        }).isInstanceOf(DenidedTokenException.class);

        verify(ipBlockManager).increase(httpServletRequest);
    }

    /* TODO: 2022-09-08 양방향 rsa256 utill class를 만들어서 openidconnect를 테스트 / 블로깅
     */


}