package toy.bookchat.bookchat.security.token.jwt;

import static toy.bookchat.bookchat.utils.constants.AuthConstants.AUTHORIZATION;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEARER;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEGIN_INDEX;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.token.TokenManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /* TODO: 2022-09-28 test
     */
    private final TokenManager jwtTokenManager;
    private final UserRepository userRepository;
    private final IpBlockManager ipBlockManager;

    public JwtAuthenticationFilter(@Qualifier("jwtTokenManager") TokenManager jwtTokenManager, UserRepository userRepository,
                                   IpBlockManager ipBlockManager) {
        this.jwtTokenManager = jwtTokenManager;
        this.userRepository = userRepository;
        this.ipBlockManager = ipBlockManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String oAuth2MemberNumber = jwtTokenManager.getOAuth2MemberNumberFromToken(
            getJwtTokenFromRequest(request), null);

        registerUserAuthenticationOnSecurityContext(userRepository.findByName(oAuth2MemberNumber));

        filterChain.doFilter(request, response);
    }

    private String getJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        ipBlockManager.increase(request);
        throw new DenidedTokenException("Not Allowed Format Request Exception");
    }

    private void registerUserAuthenticationOnSecurityContext(Optional<User> optionalUser) {
        User user = optionalUser.orElseThrow(() -> {
            throw new UserNotFoundException("Not Registered User");
        });

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        SecurityContextHolder
            .getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, null,
                userPrincipal.getAuthorities()));
    }
}
