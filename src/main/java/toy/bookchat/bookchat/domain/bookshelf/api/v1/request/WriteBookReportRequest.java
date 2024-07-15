package toy.bookchat.bookchat.domain.bookshelf.api.v1.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WriteBookReportRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @Builder
    private WriteBookReportRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public BookReport toTarget() {
        return BookReport.builder()
            .title(this.title)
            .content(this.content)
            .build();
    }
}
