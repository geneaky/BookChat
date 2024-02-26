package toy.bookchat.bookchat.domain.device.controller.dto.request;

import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateFcmTokenRequest {

    @NotEmpty
    private String fcmToken;

    @Builder
    private UpdateFcmTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
