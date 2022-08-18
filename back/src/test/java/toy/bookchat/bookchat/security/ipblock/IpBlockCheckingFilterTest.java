package toy.bookchat.bookchat.security.ipblock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpBlockCheckingFilterTest {

    @Mock
    IpBlockManager ipBlockManager;

    @InjectMocks
    IpBlockCheckingFilter ipBlockCheckingFilter;

    @Test
    public void IP가_block일때_다음_필터를_타고_진행_실패() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(ipBlockManager.validateRequest(request)).thenReturn(false);
        when(response.getWriter()).thenReturn(writer);

        ipBlockCheckingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain,never()).doFilter(request,response);
    }

    @Test
    public void IP가_block이_아닐때_다음_필터를_타고_진행_성공() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(ipBlockManager.validateRequest(request)).thenReturn(true);

        ipBlockCheckingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request,response);
    }
}