package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@ExtendWith(MockitoExtension.class)
class BookShelfManagerTest {

    @Mock
    private BookShelfRepository bookShelfRepository;
    @Mock
    private BookReportRepository bookReportRepository;
    @Mock
    private AgonyRepository agonyRepository;
    @Mock
    private AgonyRecordRepository agonyRecordRepository;
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

    @Test
    void 서재를_저장한다() throws Exception {
        BookShelf bookShelf = BookShelf.builder()
            .build();

        bookShelfManager.store(bookShelf);

        verify(bookShelfRepository).save(bookShelf);
    }

    @Test
    void 서재와_관련_기록들을_비운다() throws Exception {
        bookShelfManager.vacate(1L, 2L);

        verify(bookShelfRepository).deleteBookShelfByIdAndUserId(any(), any());
        verify(agonyRecordRepository).deleteByBookShelfIdAndUserId(any(), any());
        verify(agonyRepository).deleteByBookShelfIdAndUserId(any(), any());
    }

    @Test
    void 사용자의_서재를_전부_삭제한다() throws Exception {
        bookShelfManager.remove(1L);

        verify(bookShelfRepository).deleteAllByUserId(any());
    }
}