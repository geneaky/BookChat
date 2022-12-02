import handler.CustomExceptionHandlingFilter;
import handler.RestAuthenticationEntryPoint;
import ipblock.IpBlockCheckingFilter;
import ipblock.IpBlockManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import token.jwt.JwtAuthenticationFilter;
import token.jwt.JwtTokenManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final IpBlockManager ipBlockManager;
    private final JwtTokenManager jwtTokenManager;

    public SecurityConfig(IpBlockManager ipBlockManager, JwtTokenManager jwtTokenManager) {
        this.ipBlockManager = ipBlockManager;
        this.jwtTokenManager = jwtTokenManager;
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
            new JwtAuthenticationFilter(jwtTokenManager, ipBlockManager),
            UsernamePasswordAuthenticationFilter.class);

        http.addFilterAfter(
            new IpBlockCheckingFilter(ipBlockManager),
            UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable();
        http.formLogin().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.exceptionHandling()
            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .and()
            .authorizeRequests()
            .antMatchers("/v1/api/users/profile/nickname", "/v1/api/users/signup",
                "/v1/api/users/signin", "/v1/api/auth/token", "/v2/api/test/users/token")
            .permitAll()
            .anyRequest().authenticated();
    }
}
