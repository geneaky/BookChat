package toy.bookchat.bookchat.domain.agony.service.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBookAgonyRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String hexColorCode;

}
