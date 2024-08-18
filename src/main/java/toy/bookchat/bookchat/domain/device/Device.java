package toy.bookchat.bookchat.domain.device;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Device {

  private Long id;
  private String fcmToken;

  @Builder
  private Device(Long id, String fcmToken) {
    this.id = id;
    this.fcmToken = fcmToken;
  }

  public void changeFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }
}
