package toy.bookchat.bookchat.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.CustomExceptionHandlingFilter;
import toy.bookchat.bookchat.security.handler.RestAuthenticationEntryPoint;
import toy.bookchat.bookchat.security.ipblock.IpBlockCheckingFilter;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.token.TokenManager;
import toy.bookchat.bookchat.security.token.jwt.JwtAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final IpBlockManager ipBlockManager;
    private final TokenManager jwtTokenManager;
    private final UserRepository userRepository;

    public SecurityConfig(IpBlockManager ipBlockManager, @Qualifier("jwtTokenManager")TokenManager jwtTokenManager, UserRepository userRepository) {
        this.ipBlockManager = ipBlockManager;
        this.jwtTokenManager = jwtTokenManager;
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /*
         * custom filter를 bean으로 등록해두면 websecurity configure설정에서 security filter chain에서는 제외되지만 defautl chain에는 포함되므로 직접 생성하여 등록해줌 - 블로깅, ip 차단이랑 같이
         * https://stackoverflow.com/questions/39152803/spring-websecurity-ignoring-doesnt-ignore-custom-filter/40969780#40969780
         * */
        http.addFilterBefore(
            new CustomExceptionHandlingFilter(),
            UsernamePasswordAuthenticationFilter.class);

        http.addFilterAt(
            new JwtAuthenticationFilter(jwtTokenManager, userRepository, ipBlockManager),
            UsernamePasswordAuthenticationFilter.class);

        http.addFilterAfter(
            new IpBlockCheckingFilter(ipBlockManager),
            UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable();
        http.anonymous().disable();
        http.formLogin().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.exceptionHandling()
            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .and()
            .authorizeRequests()
            .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.GET, "/v1/api/users/profile/nickname")
            .antMatchers(HttpMethod.POST, "/v1/api/users/signup","/v1/api/users/signin");
    }
}
