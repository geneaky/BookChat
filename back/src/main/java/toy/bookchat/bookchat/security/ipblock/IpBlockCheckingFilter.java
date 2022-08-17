package toy.bookchat.bookchat.security.ipblock;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class IpBlockCheckingFilter extends OncePerRequestFilter {

    private final IpBlockManager ipBlockManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        if (!ipBlockManager.validateRequest(request)) {
            response.setStatus(401);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("you are blocked");
            return;
            // TODO: 2022/08/18 여기서 이렇게 처리할게 아니라 위의 exceptionhandlingfilter같은걸 만들어서 거기서 block ip 처리하고 , 해외 ip 차단에 따른 처리도 거기서 하자
//            throw new BlockedIpException("you are blocked");
        }
        filterChain.doFilter(request, response);
    }
}
