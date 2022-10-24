package toy.bookchat.bookchat.domain.agony.service.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBookAgonyRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

}
