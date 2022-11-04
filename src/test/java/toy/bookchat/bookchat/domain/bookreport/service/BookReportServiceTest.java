package toy.bookchat.bookchat.domain.bookreport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.WriteBookReportRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookReportServiceTest {

    @Mock
    private BookReportRepository bookReportRepository;
    @Mock
    private BookShelfRepository bookShelfRepository;
    @InjectMocks
    private BookReportService bookReportService;

    private static WriteBookReportRequestDto getWriteBookReportRequestDto() {
        return WriteBookReportRequestDto.builder()
            .title("어렵지만 많이 배웠습니다")
            .content("이런이런 저런저런 내용")
            .build();
    }

    @Test
    void 독후감_등록_성공() throws Exception {
        WriteBookReportRequestDto writeBookReportRequestDto = getWriteBookReportRequestDto();

        User user = mock(User.class);
        BookShelf bookShelf = mock(BookShelf.class);

        when(user.getId()).thenReturn(1L);
        when(bookShelfRepository.findByUserIdAndBookId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookReportService.writeReport(writeBookReportRequestDto, 1L, user.getId());

        verify(bookReportRepository).save(any());
    }

    @Test
    void 독후감_등록시_서재_독서완료_변경_성공() throws Exception {
        WriteBookReportRequestDto writeBookReportRequestDto = getWriteBookReportRequestDto();

        User user = mock(User.class);
        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(ReadingStatus.READING)
            .build();

        when(user.getId()).thenReturn(1L);
        when(bookShelfRepository.findByUserIdAndBookId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookReportService.writeReport(writeBookReportRequestDto, 1L, user.getId());

        ReadingStatus result = bookShelf.getReadingStatus();
        assertThat(result).isEqualTo(ReadingStatus.COMPLETE);
    }

    @Test
    void 서재에_등록되지_않는_책_독후감_작성시도시_예외발생() throws Exception {
        WriteBookReportRequestDto writeBookReportRequestDto = getWriteBookReportRequestDto();
        User user = mock(User.class);

        assertThatThrownBy(() -> {
            bookReportService.writeReport(writeBookReportRequestDto, 1L, user.getId());
        }).isInstanceOf(BookNotFoundException.class);
    }
}