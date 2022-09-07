package toy.bookchat.bookchat.security.openid;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static toy.bookchat.bookchat.security.jwt.JwtTokenValidationCode.*;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.*;

public class OpenIdAuthenticationFilter extends OncePerRequestFilter {

    private final OpenIdTokenManager openIdTokenManager;
    private final UserRepository userRepository;
    private final IpBlockManager ipBlockManager;

    public OpenIdAuthenticationFilter(OpenIdTokenManager openIdTokenManager, UserRepository userRepository, IpBlockManager ipBlockManager) {
        this.openIdTokenManager = openIdTokenManager;
        this.userRepository = userRepository;
        this.ipBlockManager = ipBlockManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String opendIdToken = getOpenIdTokenFromRequest(request);

        validUserRequestUsingOpenIdToken(opendIdToken);
    }

    private void validUserRequestUsingOpenIdToken(String opendIdToken) {
        if (StringUtils.hasText(opendIdToken) && openIdTokenManager.isNotValidatedToken(opendIdToken) == ACCESS) {
            String email = openIdTokenManager.getEmailFromToken(opendIdToken);
            OAuth2Provider oAuth2TokenProvider = openIdTokenManager.getOauth2TokenProviderFromToken(
                    opendIdToken);

            Optional<User> optionalUser = userRepository.findByEmailAndProvider(email,
                    oAuth2TokenProvider);
            optionalUser.ifPresentOrElse((user -> registerUserAuthentication(request, user)),
                    () -> {
                        throw new UserNotFoundException("Not Registered User Request");
                    });
        }

        if(StringUtils.hasText(opendIdToken) && openIdTokenManager.validateToken(opendIdToken) == EXPIRED) {
            // TODO: 2022-08-17 refresh token android에 요청하도록 응답 에러 세팅
            ipBlockManager.increase(request);
        }

        if(StringUtils.hasText(opendIdToken) && openIdTokenManager.validateToken(opendIdToken) == DENIED) {
            ipBlockManager.increase(request);
        }

        if(!StringUtils.hasText(opendIdToken)) { //인증된 사용자만 가능한 요청에 토큰자체를 담지 않는 경우, 인증이 필요없는 rest api의 경우 카운트하면안되는데 여길 통과하게됨
            ipBlockManager.increase(request);
        }
    }

    private String getOpenIdTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        return null;
    }


}
