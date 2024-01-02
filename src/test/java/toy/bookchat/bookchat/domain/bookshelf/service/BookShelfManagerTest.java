package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookReportRepository;

@ExtendWith(MockitoExtension.class)
class BookShelfManagerTest {

    @Mock
    private BookReportRepository bookReportRepository;
    @InjectMocks
    private BookShelfManager bookShelfManager;

    @Test
    void 책장에_독후감을_추가한다() throws Exception {
        BookShelf bookShelf = BookShelf.builder()
            .build();

        BookReport bookReport = BookReport.builder()
            .build();

        bookShelfManager.append(bookShelf, bookReport);

        assertThat(bookShelf.getBookReport()).isEqualTo(bookReport);
        verify(bookReportRepository).save(bookReport);
    }
}