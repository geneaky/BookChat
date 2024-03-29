package toy.bookchat.bookchat.domain.user.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.device.Device;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignInRequest {

    @NotNull
    @JsonProperty("oauth2Provider")
    private OAuth2Provider oauth2Provider;
    @NotBlank
    private String fcmToken;
    @NotBlank
    private String deviceToken;
    private Boolean approveChangingDevice;

    @Builder
    private UserSignInRequest(OAuth2Provider oauth2Provider, String fcmToken, String deviceToken,
        Boolean approveChangingDevice) {
        this.oauth2Provider = oauth2Provider;
        this.fcmToken = fcmToken;
        this.deviceToken = deviceToken;
        this.approveChangingDevice = approveChangingDevice;
    }

    public Device createDevice(User user) {
        return Device.builder()
            .deviceToken(this.deviceToken)
            .fcmToken(this.fcmToken)
            .user(user)
            .build();
    }

    public boolean approved() {
        return this.approveChangingDevice != null && this.approveChangingDevice;
    }
}