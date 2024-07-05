package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookReportTitleAndContent;

@ExtendWith(MockitoExtension.class)
class BookReportServiceTest {

    @Mock
    private BookShelfManager bookShelfManager;
    @Mock
    private BookShelfReader bookShelfReader;
    @Mock
    private BookReportAppender bookReportAppender;
    @Mock
    private BookReportReader bookReportReader;
    @Mock
    private BookReportManager bookReportManager;
    @InjectMocks
    private BookReportService bookReportService;

    private static BookReportEntity getBookReport() {
        return BookReportEntity.builder()
            .title("title")
            .content("content")
            .build();
    }

    @Test
    void 독후감_등록_성공() throws Exception {
        BookReport bookReport = BookReport.builder()
            .title("k9Zb13TZXZ")
            .content("F7s2SbZw")
            .build();
        bookReportService.writeReport(1L, 1L, bookReport);

        verify(bookShelfReader).readBookShelf(any(), any());
        verify(bookReportAppender).append(any(), any());
        verify(bookShelfManager).modify(any(), any());
    }


    @Test
    void 서재에_등록된_책_독후감_조회_성공() throws Exception {
        bookReportService.getBookReport(1L, 1L);
        verify(bookReportReader).readBookReport(any(), any());
    }

    @Test
    void 서재에_등록된_책_독후감_삭제_성공() throws Exception {
        bookReportService.deleteBookReport(1L, 1L);
        verify(bookReportManager).delete(any());
    }

    @Test
    void 서재에_등록된_책_독후감_수정_성공() throws Exception {
        BookReportTitleAndContent bookReportTitleAndContent = BookReportTitleAndContent.builder().build();
        bookReportService.reviseBookReport(1L, 1L, bookReportTitleAndContent);
        verify(bookReportManager).modify(any(), any(), any());
    }
}