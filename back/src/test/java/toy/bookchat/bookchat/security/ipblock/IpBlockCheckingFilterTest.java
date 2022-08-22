package toy.bookchat.bookchat.security.ipblock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.security.ipblock.exception.BlockedIpException;

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
        FilterChain filterChain = mock(FilterChain.class);

        when(ipBlockManager.validateRequest(request)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> {
            ipBlockCheckingFilter.doFilterInternal(request, response, filterChain);
        }).isInstanceOf(BlockedIpException.class);
    }

    @Test
    public void IP가_block이_아닐때_다음_필터를_타고_진행_성공() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(ipBlockManager.validateRequest(request)).thenReturn(true);

        ipBlockCheckingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}