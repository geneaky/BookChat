package toy.bookchat.bookchat.security.jwt;

import static toy.bookchat.bookchat.security.jwt.JwtTokenValidationCode.ACCESS;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.AUTHORIZATION;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEARER;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final int BEGIN_INDEX = 7;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String jwt = getJwtFromRequest(request);

        validUserRequestByJwt(request, jwt);

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        return null;
    }

    private void validUserRequestByJwt(HttpServletRequest request, String jwt) {
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt) == ACCESS) {
            String email = jwtTokenProvider.getEmailFromToken(jwt);
            OAuth2Provider oAuth2TokenProvider = jwtTokenProvider.getOauth2TokenProviderFromToken(
                jwt);

            Optional<User> optionalUser = userRepository.findByEmailAndProvider(email,
                oAuth2TokenProvider);
            optionalUser.ifPresentOrElse((user -> registerUserAuthentication(request, user)),
                () -> {
                    throw new UserNotFoundException("Not Registered User Request");
                });
        }
    }

    private void registerUserAuthentication(HttpServletRequest request,
        User user) {
        UserDetails userDetails = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
