package toy.bookchat.bookchat.security.ipblock;

import com.querydsl.core.util.StringUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IpBlockManager {

    public static final long ONE_DAY = 1800; // 30분으로 임시 변경
    public static final int LIMITED_COUNT = 1000;
    private final AccessIpRepository accessIpRepository;

    public IpBlockManager(AccessIpRepository accessIpRepository) {
        this.accessIpRepository = accessIpRepository;
    }

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
            if (Duration.between(accessIp.getUpdatedAt(), LocalDateTime.now()).getSeconds()
                > ONE_DAY) {
                accessIp.reset();
                return;
            }
            accessIp.increase();
        }, () -> {
            AccessIp accessIp = new AccessIp(X_Forwarded_For, 0L);
            accessIpRepository.save(accessIp);
        });
    }

    public boolean validateRequest(HttpServletRequest request) {
        final String header = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNullOrEmpty(header)) {
            Optional<AccessIp> optionalAccessIp = accessIpRepository.findById(
                request.getRemoteAddr());
            return optionalAccessIp.map(accessIp -> accessIp.getAccessFailCount() < LIMITED_COUNT)
                .orElse(true);
        }
        Optional<AccessIp> optionalAccessIp = accessIpRepository.findById(header);
        return optionalAccessIp.map(accessIp -> accessIp.getAccessFailCount() < LIMITED_COUNT)
            .orElse(true);
    }
}
