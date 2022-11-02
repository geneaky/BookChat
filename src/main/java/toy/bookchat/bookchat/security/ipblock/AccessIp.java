package toy.bookchat.bookchat.security.ipblock;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessIp extends BaseEntity {

    @Id
    private String ip;
    private Long accessFailCount;

    public AccessIp(String ip, Long accessFailCount) {
        this.ip = ip;
        this.accessFailCount = accessFailCount;
    }

    public void increase() {
        this.accessFailCount++;
    }

    public void reset() {
        this.accessFailCount = 1L;
    }

    public Long getAccessFailCount() {
        return accessFailCount;
    }
}
