package toy.bookchat.bookchat.security.ipblock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpBlockManagerTest {

    @Mock
    AccessIpRepository accessIpRepository;

    @InjectMocks
    IpBlockManager ipBlockManager;

    @Test
    public void XFF가없을때_지정된_횟수보다_더_많이_유효하지않은_요청을_보낸경우_false() throws Exception {
        AccessIp accessIp = new AccessIp("0.0.0.0",101L);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(accessIpRepository.findById("0.0.0.0")).thenReturn(Optional.of(accessIp));
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("0.0.0.0");

        boolean result = ipBlockManager.validateRequest(request);
        assertThat(result).isFalse();
    }

    @Test
    public void XFF가있을때_지정된_횟수보다_적게_유효하지않은_요청을_보낸경우_true() throws Exception {
        AccessIp accessIp = new AccessIp("0.0.0.0",1L);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(accessIpRepository.findById("0.0.0.0")).thenReturn(Optional.of(accessIp));
        when(request.getHeader("X-Forwarded-For")).thenReturn("0.0.0.0");

        boolean result = ipBlockManager.validateRequest(request);
        assertThat(result).isTrue();
    }

    @Test
    public void 마지막_실패_요청으로부터_하루가_지난경우_실패횟수를_초기화() throws Exception {
        AccessIp accessIp = new AccessIp("0.0.0.0",100L);
        accessIp.setUpdatedAt(LocalDateTime.now().minusDays(1L).minusMinutes(5L));

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(accessIpRepository.findById("0.0.0.0")).thenReturn(Optional.of(accessIp));
        when(request.getHeader("X-Forwarded-For")).thenReturn("0.0.0.0");

        ipBlockManager.increase(request);

        assertThat(accessIp.getAccessFailCount()).isOne();
    }
}