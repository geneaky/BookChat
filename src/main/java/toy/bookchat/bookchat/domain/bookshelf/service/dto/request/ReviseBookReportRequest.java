package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseBookReportRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @Builder
    private ReviseBookReportRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void revise(BookReportEntity bookReportEntity) {
        bookReportEntity.reviseTitle(title);
        bookReportEntity.reviseContent(content);
    }
}
