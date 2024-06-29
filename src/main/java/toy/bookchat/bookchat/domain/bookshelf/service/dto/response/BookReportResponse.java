package toy.bookchat.bookchat.domain.bookshelf.service.dto.response;

import lombok.Getter;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;

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

    public static BookReportResponse from(BookReportEntity bookReportEntity) {
        return new BookReportResponse(bookReportEntity.getTitle(), bookReportEntity.getContent(),
            bookReportEntity.getCreateTimeInYearMonthDayFormat());
    }
}
