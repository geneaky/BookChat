package toy.bookchat.bookchat.db_module.device;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.BaseEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;

@Getter
@Entity
@Table(name = "device")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceToken;
    private String fcmToken;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Builder
    private DeviceEntity(Long id, String deviceToken, String fcmToken, UserEntity userEntity) {
        this.id = id;
        this.deviceToken = deviceToken;
        this.fcmToken = fcmToken;
        this.userEntity = userEntity;
    }

    public void changeDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void changeFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
