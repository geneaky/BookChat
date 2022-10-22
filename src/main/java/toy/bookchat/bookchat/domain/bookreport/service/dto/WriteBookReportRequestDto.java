package toy.bookchat.bookchat.domain.bookreport.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WriteBookReportRequestDto {

    @NotNull
    private Long bookShelfId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String hexColorCode;
}
