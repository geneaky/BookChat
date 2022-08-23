package toy.bookchat.bookchat.security.ipblock;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.security.ipblock.exception.BlockedIpException;

public class IpBlockCheckingFilter extends OncePerRequestFilter {

    private final IpBlockManager ipBlockManager;

    public IpBlockCheckingFilter(IpBlockManager ipBlockManager) {
        this.ipBlockManager = ipBlockManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        if (!ipBlockManager.validateRequest(request)) {
            throw new BlockedIpException("you are blocked");
        }
        filterChain.doFilter(request, response);
    }
}
