package toy.bookchat.bookchat.domain.bookshelf.api.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Getter;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;

@Getter
public class BookReportResponse {

    private String reportTitle;
    private String reportContent;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportCreatedAt;

    private BookReportResponse(String reportTitle, String reportContent, LocalDate reportCreatedAt) {
        this.reportTitle = reportTitle;
        this.reportContent = reportContent;
        this.reportCreatedAt = reportCreatedAt;
    }

    public static BookReportResponse from(BookReport bookReport) {
        return new BookReportResponse(bookReport.getTitle(), bookReport.getContent(), bookReport.getReportedAt().toLocalDate());
    }
}
