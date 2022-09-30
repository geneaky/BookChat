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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.user.UserPrincipal;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /* TODO: 2022-09-28 test
     */
    private final JwtTokenManager jwtTokenManager;
    private final UserRepository userRepository;
    private final IpBlockManager ipBlockManager;

    public JwtAuthenticationFilter(JwtTokenManager jwtTokenManager, UserRepository userRepository,
                                   IpBlockManager ipBlockManager) {
        this.jwtTokenManager = jwtTokenManager;
        this.userRepository = userRepository;
        this.ipBlockManager = ipBlockManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            authentication(request);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void authentication(HttpServletRequest request) {
        String oAuth2MemberNumber = jwtTokenManager.getOAuth2MemberNumberFromToken(
                getJwtTokenFromRequest(request));

        registerUserAuthenticationOnSecurityContext(userRepository.findByName(oAuth2MemberNumber));
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

        /* TODO: 2022-09-29 open-session-in-view설정 꺼놨는데 여기서 데이터베이스에서 사용자를 조회해서 넣어줄 필요가 있을까?
            그냥 토큰에 있는 정보로만 UserPrincipal 만들어서 등록할까 service layer에서 필요할때만 사용자 조회하는게
            매번 사용자 조회하는 것 보다 비용이 더 저렴할듯
         */
        UserPrincipal userPrincipal = UserPrincipal.create(user);

        SecurityContextHolder
            .getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, null,
                userPrincipal.getAuthorities()));
    }
}
