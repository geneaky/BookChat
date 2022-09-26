package toy.bookchat.bookchat.security.exception;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.security.ipblock.exception.BlockedIpException;

public class CustomExceptionHandlingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (UserNotFoundException exception) {
            response.setStatus(401);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("Not Registered User Request");
        } catch (BlockedIpException exception) {
            response.setStatus(403);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("You Are Blocked");
        } catch (ExpiredTokenException exception) {
            response.setStatus(401);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("Token Expired");
        } catch (DenidedTokenException exception) {
            response.setStatus(401);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("Not Valid Token Request");
        } catch (NotVerifiedRequestFormatException exception) {
            response.setStatus(401);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("Empty Provider Type");
        }
    }
}
