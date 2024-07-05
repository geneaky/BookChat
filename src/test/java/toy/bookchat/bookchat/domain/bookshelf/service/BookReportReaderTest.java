package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;

@ExtendWith(MockitoExtension.class)
class BookReportReaderTest {

    @Mock
    private BookReportRepository bookReportRepository;
    @InjectMocks
    private BookReportReader bookReportReader;

    @Test
    void 독후감_조회_성공() throws Exception {
        BookReportEntity bookReportEntity = BookReportEntity.builder().id(1L).build();
        given(bookReportRepository.findByUserIdAndBookShelfId(any(), any())).willReturn(bookReportEntity);
        BookReport bookReport = bookReportReader.readBookReport(1L, 1L);

        assertThat(bookReport.getId()).isEqualTo(1L);
    }
}