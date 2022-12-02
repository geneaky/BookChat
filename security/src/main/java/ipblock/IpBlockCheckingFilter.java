package ipblock;

import exception.BlockedIpException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class IpBlockCheckingFilter extends OncePerRequestFilter {

    private final IpBlockManager ipBlockManager;

    public IpBlockCheckingFilter(IpBlockManager ipBlockManager) {
        this.ipBlockManager = ipBlockManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        checkIfBlockedUser(request);
        filterChain.doFilter(request, response);
    }

    private void checkIfBlockedUser(HttpServletRequest request) {
        if (!ipBlockManager.validateRequest(request)) {
            throw new BlockedIpException();
        }
    }
}
