package toy.bookchat.bookchat.domain.device.api.v1;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.device.api.v1.request.UpdateFcmTokenRequest;
import toy.bookchat.bookchat.domain.device.service.DeviceService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api/devices")
public class DeviceController {

  private final DeviceService deviceService;

  public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @PutMapping("/fcm-token")
  public void updateFcmToken(@UserPayload TokenPayload tokenPayload,
      @Valid @RequestBody UpdateFcmTokenRequest request) {
    deviceService.updateFcmToken(tokenPayload.getUserId(), request.getFcmToken());
  }
}
