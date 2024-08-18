package toy.bookchat.bookchat.db_module.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@Table(name = "device")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id", nullable = false)
  private Long userId;
  private String deviceToken;
  private String fcmToken;

  @Builder
  private DeviceEntity(Long id, Long userId, String deviceToken, String fcmToken) {
    this.id = id;
    this.userId = userId;
    this.deviceToken = deviceToken;
    this.fcmToken = fcmToken;
  }

  public void changeDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  public void changeFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }
}
