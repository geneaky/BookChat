package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookReportTitleAndContent;

@ExtendWith(MockitoExtension.class)
class BookReportManagerTest {

    @Mock
    private BookReportRepository bookReportRepository;
    @InjectMocks
    private BookReportManager bookReportManager;

    @Test
    void 독후감_삭제_성공() throws Exception {
        BookReport bookReport = BookReport.builder().build();
        bookReportManager.delete(bookReport);

        verify(bookReportRepository).deleteById(any());
    }

    @Test
    void 독후감_수정_성공() throws Exception {
        BookReportEntity bookReportEntity = BookReportEntity.builder().title("title1").content("content1").build();
        given(bookReportRepository.findByUserIdAndBookShelfId(any(), any())).willReturn(bookReportEntity);

        BookReportTitleAndContent bookReportTitleAndContent = BookReportTitleAndContent.builder().title("title2").content("content2").build();
        bookReportManager.modify(1L, 1L, bookReportTitleAndContent);

        assertThat(bookReportEntity.getTitle()).isEqualTo("title2");
        assertThat(bookReportEntity.getContent()).isEqualTo("content2");
    }
}