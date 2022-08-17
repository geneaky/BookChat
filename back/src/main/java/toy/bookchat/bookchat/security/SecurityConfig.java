package toy.bookchat.bookchat.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationFailureHandler;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationSuccessHandler;
import toy.bookchat.bookchat.security.handler.RestAuthenticationEntryPoint;
import toy.bookchat.bookchat.security.jwt.JwtAuthenticationFilter;
import toy.bookchat.bookchat.security.oauth.CustomOAuth2UserService;
import toy.bookchat.bookchat.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //세션 없이

        http.addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.anonymous().disable();

        http.authorizeHttpRequests()
            .antMatchers("/", "/auth", "/app","/v1/api/users/profile/nickname").permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .formLogin().disable()
            .oauth2Login()
            .authorizationEndpoint()
            .baseUri("/oauth2/authorize")
            .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
            .and()
            .redirectionEndpoint()
            .baseUri("/oauth2/callback/*")
            .and()
            .userInfoEndpoint()
            .userService(customOAuth2UserService)
            .and()
            .successHandler(customAuthenticationSuccessHandler)
            .failureHandler(customAuthenticationFailureHandler);
//        failureHandler 만들어야함

    }
}
