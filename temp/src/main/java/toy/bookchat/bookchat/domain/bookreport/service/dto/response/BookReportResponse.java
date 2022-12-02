package toy.bookchat.bookchat.domain.bookreport.service.dto.response;

import lombok.Getter;
import toy.bookchat.bookchat.domain.bookreport.BookReport;

@Getter
public class BookReportResponse {

    private String reportTitle;
    private String reportContent;
    private String reportCreatedAt;

    private BookReportResponse(String reportTitle, String reportContent,
        String reportCreatedAt) {
        this.reportTitle = reportTitle;
        this.reportContent = reportContent;
        this.reportCreatedAt = reportCreatedAt;
    }

    public static BookReportResponse from(BookReport bookReport) {
        return new BookReportResponse(bookReport.getTitle(), bookReport.getContent(),
            bookReport.getCreateTimeInYearMonthDayFormat());
    }
}
