package toy.bookchat.bookchat.domain.bookreport.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookreport.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WriteBookReportRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    public BookReport getBookReport(BookShelf bookShelf) {
        return BookReport.builder()
            .title(this.title)
            .content(this.content)
            .bookShelf(bookShelf)
            .build();
    }

    @Builder
    private WriteBookReportRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
