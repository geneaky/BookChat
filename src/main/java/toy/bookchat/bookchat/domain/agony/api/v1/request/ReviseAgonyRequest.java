package toy.bookchat.bookchat.domain.agony.api.v1.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseAgonyRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

    @Builder
    private ReviseAgonyRequest(String title, String hexColorCode) {
        this.title = title;
        this.hexColorCode = hexColorCode;
    }
}
