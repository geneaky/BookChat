package toy.bookchat.bookchat.security.openid;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.NotVerifiedRequestFormatException;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.user.UserPrincipal;

import static toy.bookchat.bookchat.utils.constants.AuthConstants.*;

public class OpenIdAuthenticationFilter extends OncePerRequestFilter {

    private final OpenIdTokenManager openIdTokenManager;
    private final UserRepository userRepository;
    private final IpBlockManager ipBlockManager;

    public OpenIdAuthenticationFilter(OpenIdTokenManager openIdTokenManager,
        UserRepository userRepository, IpBlockManager ipBlockManager) {
        this.openIdTokenManager = openIdTokenManager;
        this.userRepository = userRepository;
        this.ipBlockManager = ipBlockManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        OAuth2Provider openIdTokenProvider = getOpenIdTokenProviderFromRequest(request);
        String openIdToken = getOpenIdTokenFromRequest(request);

        String oAuth2MemberNumber = openIdTokenManager.getOAuth2MemberNumberFromOpenIdToken(
            openIdTokenProvider.getValue(), openIdToken);

        Optional<User> optionalUser = userRepository.findByName(oAuth2MemberNumber);

        registerUserAuthenticationOnSecurityContext(optionalUser);

        filterChain.doFilter(request, response);
    }

    private OAuth2Provider getOpenIdTokenProviderFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(OAuth2Provider.from(request.getHeader(PROVIDER_TYPE)))
                .orElseThrow(() -> {throw new NotVerifiedRequestFormatException("Empty Provider Type");});
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

    private String getOpenIdTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        throw new DenidedTokenException("Not Allowed Format Request Exception");
    }


}
