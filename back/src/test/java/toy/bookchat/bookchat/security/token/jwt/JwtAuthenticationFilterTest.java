package toy.bookchat.bookchat.security.token.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.TokenManager;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    TokenManager tokenManager;

    @Mock
    UserRepository userRepository;

    @Mock
    IpBlockManager ipBlockManager;

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void jwt인증성공시_다음_필터_호출() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        User user = User.builder()
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
        when(userRepository.findByName(any())).thenReturn(Optional.of(user));
        when(tokenManager.getOAuth2MemberNumberFromToken(any(), any())).thenReturn("userName");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}