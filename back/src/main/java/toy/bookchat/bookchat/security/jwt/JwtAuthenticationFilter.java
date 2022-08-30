package toy.bookchat.bookchat.security.jwt;

import static toy.bookchat.bookchat.security.jwt.JwtTokenValidationCode.*;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.*;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;
import toy.bookchat.bookchat.utils.constants.AuthConstants;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final IpBlockManager ipBlockManager;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, IpBlockManager ipBlockManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.ipBlockManager = ipBlockManager;
    }

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

        if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt) == EXPIRED) {
            // TODO: 2022-08-17 refresh token android에 요청하도록 응답 에러 세팅
            ipBlockManager.increase(request);
        }

        if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt) == DENIED) {
            ipBlockManager.increase(request);
        }

        if(!StringUtils.hasText(jwt)) { //인증된 사용자만 가능한 요청에 토큰자체를 담지 않는 경우, 인증이 필요없는 rest api의 경우 카운트하면안되는데 여길 통과하게됨
            ipBlockManager.increase(request);
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
