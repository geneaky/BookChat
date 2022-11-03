package toy.bookchat.bookchat.exception.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.exception.ipblock.BlockedIpException;

public class CustomExceptionHandlingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (BlockedIpException exception) {
            response.setStatus(403);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("You Are Blocked");
        }
    }
}
