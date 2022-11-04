package toy.bookchat.bookchat.domain.agony.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseAgonyRequest {

    @NotBlank
    private String agonyTitle;
    @NotBlank
    private String agonyColor;

    @Builder
    private ReviseAgonyRequest(String agonyTitle, String agonyColor) {
        this.agonyTitle = agonyTitle;
        this.agonyColor = agonyColor;
    }
}
