package toy.bookchat.bookchat.domain.device;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceToken;
    private String fcmToken;
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    private Device(Long id, String deviceToken, String fcmToken, User user) {
        this.id = id;
        this.deviceToken = deviceToken;
        this.fcmToken = fcmToken;
        this.user = user;
    }

    public void changeDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void changeFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
