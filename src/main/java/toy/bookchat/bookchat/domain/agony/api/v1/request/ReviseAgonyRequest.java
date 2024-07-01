package toy.bookchat.bookchat.domain.agony.api.v1.request;

import static lombok.AccessLevel.PRIVATE;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.agony.AgonyTitleAndColorCode;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class ReviseAgonyRequestV2 {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

    @Builder
    private ReviseAgonyRequestV2(String title, String hexColorCode) {
        this.title = title;
        this.hexColorCode = hexColorCode;
    }

    public AgonyTitleAndColorCode toTarget() {
        return AgonyTitleAndColorCode.builder()
            .title(this.title)
            .hexColorCode(this.hexColorCode)
            .build();
    }
}
