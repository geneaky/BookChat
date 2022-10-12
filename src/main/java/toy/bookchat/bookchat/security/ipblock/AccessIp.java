package toy.bookchat.bookchat.security.ipblock;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccessIp extends BaseEntity {

    @Id
    private String ip;
    private Long accessFailCount;

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
