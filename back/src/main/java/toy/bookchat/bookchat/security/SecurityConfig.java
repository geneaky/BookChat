package toy.bookchat.bookchat.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.CustomExceptionHandlingFilter;
import toy.bookchat.bookchat.security.handler.RestAuthenticationEntryPoint;
import toy.bookchat.bookchat.security.ipblock.IpBlockCheckingFilter;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.openid.OpenIdAuthenticationFilter;
import toy.bookchat.bookchat.security.openid.OpenIdTokenManager;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final IpBlockManager ipBlockManager;
    private final OpenIdTokenManager openIdTokenManager;
    private final UserRepository userRepository;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /*
         * custom filter를 bean으로 등록해두면 websecurity configure설정에서 security filter chain에서는 제외되지만 defautl chain에는 포함되므로 직접 생성하여 등록해줌 - 블로깅, ip 차단이랑 같이
         * https://stackoverflow.com/questions/39152803/spring-websecurity-ignoring-doesnt-ignore-custom-filter/40969780#40969780
         * */
        http.addFilterAt(
            new OpenIdAuthenticationFilter(openIdTokenManager, userRepository, ipBlockManager),
            UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(
            new IpBlockCheckingFilter(ipBlockManager),
            UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(
            new CustomExceptionHandlingFilter(),
            IpBlockCheckingFilter.class);

        http.csrf().disable();
        http.anonymous().disable();
        http.formLogin().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests()
            .antMatchers("/").permitAll()
            .antMatchers(HttpMethod.GET, "/v1/api/users/profile/nickname").permitAll()
            .antMatchers(HttpMethod.POST, "/v1/api/users").permitAll();

        /* TODO: 2022-09-07 이부분 정상 동작 테스트
         */
        http.authorizeHttpRequests()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(restAuthenticationEntryPoint);
    }
}
