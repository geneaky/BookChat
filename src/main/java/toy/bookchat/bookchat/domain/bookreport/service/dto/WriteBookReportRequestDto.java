package toy.bookchat.bookchat.domain.bookreport.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookreport.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

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

    public BookReport getBookReport(BookShelf bookShelf) {
        return new BookReport(this.title, this.content, this.hexColorCode, bookShelf);
    }
}
