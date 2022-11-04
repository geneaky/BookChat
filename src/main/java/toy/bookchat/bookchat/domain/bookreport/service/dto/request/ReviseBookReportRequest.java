package toy.bookchat.bookchat.domain.bookreport.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseBookReportRequest {

    @NotBlank
    private String reportTitle;
    @NotBlank
    private String reportContent;

    @Builder
    private ReviseBookReportRequest(String reportTitle, String reportContent) {
        this.reportTitle = reportTitle;
        this.reportContent = reportContent;
    }
}
