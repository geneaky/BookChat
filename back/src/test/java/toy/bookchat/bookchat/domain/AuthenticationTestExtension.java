package toy.bookchat.bookchat.domain;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationFailureHandler;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationSuccessHandler;
import toy.bookchat.bookchat.security.handler.RestAuthenticationEntryPoint;
import toy.bookchat.bookchat.security.jwt.JwtAuthenticationFilter;
import toy.bookchat.bookchat.security.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.oauth.CustomOAuth2UserService;
import toy.bookchat.bookchat.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;

@Import({JwtAuthenticationFilter.class, RestAuthenticationEntryPoint.class})
public abstract class AuthenticationTestExtension {

    @MockBean
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @MockBean
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockBean
    CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;
    @MockBean
    UserRepository userRepository;
    @MockBean
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

}
