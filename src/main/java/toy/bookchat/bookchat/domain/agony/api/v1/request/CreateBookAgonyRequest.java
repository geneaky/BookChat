package toy.bookchat.bookchat.domain.agony.api.v1.request;

import static lombok.AccessLevel.PRIVATE;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.agony.Agony;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class CreateBookAgonyRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

    @Builder
    private CreateBookAgonyRequest(String title, String hexColorCode) {
        this.title = title;
        this.hexColorCode = hexColorCode;
    }

    public Agony toTarget() {
        return Agony.builder()
            .title(this.title)
            .hexColorCode(this.hexColorCode)
            .build();
    }
}
