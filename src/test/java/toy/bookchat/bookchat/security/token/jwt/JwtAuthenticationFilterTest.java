package toy.bookchat.bookchat.security.token.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    JwtTokenManager jwtTokenManager;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void jwt인증성공시_다음_필터_호출() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        User user = User.builder()
            .id(1L)
            .nickname("testNick")
            .name("google124")
            .provider(OAuth2Provider.GOOGLE)
            .email("test@gmail.com")
            .role(ROLE.USER)
            .defaultProfileImageType(1)
            .profileImageUrl(null)
            .readingTastes(List.of(ReadingTaste.ART))
            .build();

        when(request.getHeader(any())).thenReturn("Bearer test");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}