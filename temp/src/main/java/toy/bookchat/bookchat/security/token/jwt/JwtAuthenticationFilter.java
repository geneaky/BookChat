package toy.bookchat.bookchat.security.token.jwt;

import static toy.bookchat.bookchat.domain.common.AuthConstants.BEARER;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEGIN_INDEX;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPrincipal;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final IpBlockManager ipBlockManager;

    public JwtAuthenticationFilter(JwtTokenManager jwtTokenManager, IpBlockManager ipBlockManager) {
        this.jwtTokenManager = jwtTokenManager;
        this.ipBlockManager = ipBlockManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            authentication(request);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
//            SecurityContextHolder.getContext().setAuthentication(null);
        }

        filterChain.doFilter(request, response);
    }

    private void authentication(HttpServletRequest request) {

        TokenPayload tokenPayload = jwtTokenManager.getTokenPayloadFromToken(
            getJwtTokenFromRequest(request));

        registerUserAuthenticationOnSecurityContext(tokenPayload);
    }

    private String getJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        ipBlockManager.increase(request);
        throw new DenidedTokenException();
    }

    private void registerUserAuthenticationOnSecurityContext(TokenPayload tokenPayload) {
        UserPrincipal userPrincipal = UserPrincipal.create(tokenPayload);

        SecurityContextHolder
            .getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, null,
                userPrincipal.getAuthorities()));
    }
}
