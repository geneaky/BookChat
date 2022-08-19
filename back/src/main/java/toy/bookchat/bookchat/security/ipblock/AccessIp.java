package toy.bookchat.bookchat.security.ipblock;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccessIp implements Serializable {

    @Id
    private String ip;
    private Long accessFailCount;
    private LocalDateTime accessTimeStamp;

    public void increase() {
        this.accessFailCount++;
    }

    public void reset() {
        this.accessFailCount = 1L;
    }

    public Long getAccessFailCount() {
        return accessFailCount;
    }

    public LocalDateTime getAccessTimeStamp() {
        return accessTimeStamp;
    }

    public void updateAccessTimeStamp(LocalDateTime updateAccessTimeStamp) {
        this.accessTimeStamp = updateAccessTimeStamp;
    }
}
