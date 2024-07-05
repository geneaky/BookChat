package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@ExtendWith(MockitoExtension.class)
class BookReportAppenderTest {

    @Mock
    private BookReportRepository bookReportRepository;
    @InjectMocks
    private BookReportAppender bookReportAppender;

    @Test
    void 서재에_독후감_추가_성공() throws Exception {
        BookShelf bookShelf = BookShelf.builder().build();
        BookReport bookReport = BookReport.builder().build();
        bookReportAppender.append(bookShelf, bookReport);

        verify(bookReportRepository).save(any());
    }
}