package toy.bookchat.bookchat.security.ipblock;

import com.querydsl.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IpBlockManager {

    public static final int ONE_DAY = 86400;
    public static final int LIMITED_COUNT = 10;
    private final AccessIpRepository accessIpRepository;

    @Transactional
    public void increase(HttpServletRequest request) {
        final String header = request.getHeader("X-Forwarded-For");
        if (header == null) {
            increaseAccessFailCount(request.getRemoteAddr());
            return;
        }
        increaseAccessFailCount(header.split(",")[0]);
    }

    private void increaseAccessFailCount(String X_Forwarded_For) {
        Optional<AccessIp> optionalAccessIp = accessIpRepository.findById(X_Forwarded_For);
        optionalAccessIp.ifPresentOrElse(accessIp -> {
            if(Duration.between(LocalDateTime.now(),accessIp.getAccessTimeStamp()).getSeconds() > ONE_DAY) {
                accessIp.updateAccessTimeStamp(LocalDateTime.now());
                accessIp.reset();
                return;
            }
            accessIp.increase();
        },() -> {
            AccessIp accessIp = new AccessIp(X_Forwarded_For,0L, LocalDateTime.now());
            accessIpRepository.save(accessIp);
        });
    }

    public boolean validateRequest(HttpServletRequest request) {
        final String header = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNullOrEmpty(header)) {
            Optional<AccessIp> optionalAccessIp = accessIpRepository.findById(request.getRemoteAddr());
            return optionalAccessIp.map(accessIp -> accessIp.getAccessFailCount() < LIMITED_COUNT).orElse(true);
        }
        Optional<AccessIp> optionalAccessIp = accessIpRepository.findById(header);
        return optionalAccessIp.map(accessIp -> accessIp.getAccessFailCount() < LIMITED_COUNT).orElse(true);
    }
}
