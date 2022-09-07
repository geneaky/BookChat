package toy.bookchat.bookchat.security.openid;

import static toy.bookchat.bookchat.utils.constants.AuthConstants.AUTHORIZATION;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEARER;
import static toy.bookchat.bookchat.utils.constants.AuthConstants.BEGIN_INDEX;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;

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
        String opendIdToken = getOpenIdTokenFromRequest(request);

        //사용자 resource server 회원번호와 resource server 이름(카카오, 구글)로
        //조회해서 나오면 통과 없으면 exception
        //   validUserRequestUsingOpenIdToken(opendIdToken);
    }

    private String getOpenIdTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEGIN_INDEX);
        }
        return null;
    }


}
