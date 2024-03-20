package toy.bookchat.bookchat.security.token.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPrincipal;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;

    public JwtAuthenticationFilter(JwtTokenManager jwtTokenManager) {
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            authentication(request);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void authentication(HttpServletRequest request) {
        String bearerToken = jwtTokenManager.extractTokenFromAuthorizationHeader(request.getHeader(HttpHeaders.AUTHORIZATION));
        TokenPayload tokenPayload = jwtTokenManager.getTokenPayloadFromToken(bearerToken);

        registerUserAuthenticationOnSecurityContext(tokenPayload);
    }

    private void registerUserAuthenticationOnSecurityContext(TokenPayload tokenPayload) {
        UserPrincipal userPrincipal = UserPrincipal.create(tokenPayload);

        SecurityContextHolder
            .getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()));
    }
}
