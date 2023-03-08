package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.WriteBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.BookReportResponse;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;
import toy.bookchat.bookchat.exception.bookshelf.BookReportNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookReportServiceTest {

    @Mock
    private BookReportRepository bookReportRepository;
    @Mock
    private BookShelfRepository bookShelfRepository;
    @InjectMocks
    private BookReportService bookReportService;

    private static WriteBookReportRequest getWriteBookReportRequest() {
        return WriteBookReportRequest.builder()
            .title("어렵지만 많이 배웠습니다")
            .content("이런이런 저런저런 내용")
            .build();
    }

    private static BookReport getBookReport() {
        return BookReport.builder()
            .title("title")
            .content("content")
            .build();
    }

    @Test
    void 독후감_등록_성공() throws Exception {
        WriteBookReportRequest writeBookReportRequest = getWriteBookReportRequest();

        User user = mock(User.class);
        BookShelf bookShelf = mock(BookShelf.class);

        when(user.getId()).thenReturn(1L);
        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookReportService.writeReport(writeBookReportRequest, 1L, user.getId());

        verify(bookReportRepository).save(any());
    }

    @Test
    void 독후감_등록시_서재_독서완료_변경_성공() throws Exception {
        WriteBookReportRequest writeBookReportRequest = getWriteBookReportRequest();

        User user = mock(User.class);
        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(ReadingStatus.READING)
            .build();

        when(user.getId()).thenReturn(1L);
        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookReportService.writeReport(writeBookReportRequest, 1L, user.getId());

        ReadingStatus result = bookShelf.getReadingStatus();
        assertThat(result).isEqualTo(ReadingStatus.COMPLETE);
    }

    @Test
    void 서재에_등록되지_않는_책_독후감_작성시도시_예외발생() throws Exception {
        WriteBookReportRequest writeBookReportRequest = getWriteBookReportRequest();

        assertThatThrownBy(() -> {
            bookReportService.writeReport(writeBookReportRequest, 1L, 1L);
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 서재에_등록된_책_독후감_없을시_조회하면_예외발생() throws Exception {
        BookShelf bookShelf = BookShelf.builder()
            .build();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        assertThatThrownBy(() -> {
            bookReportService.getBookReportResponse(1L, 1L);
        }).isInstanceOf(BookReportNotFoundException.class);
    }

    @Test
    void 서재에_등록된_책_독후감_조회_성공() throws Exception {
        BookReport bookReport = getBookReport();

        bookReport.setCreatedAt(LocalDateTime.now());

        BookShelf bookShelf = BookShelf.builder()
            .bookReport(bookReport)
            .build();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));
        BookReportResponse bookReportResponse = bookReportService.getBookReportResponse(1L, 1L);

        String result = bookReportResponse.getReportTitle();
        assertThat(result).isEqualTo("title");
    }

    @Test
    void 서재에_등록된_책_독후감_삭제_성공() throws Exception {
        BookReport bookReport = getBookReport();

        bookReport.setCreatedAt(LocalDateTime.now());

        BookShelf bookShelf = BookShelf.builder()
            .bookReport(bookReport)
            .build();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookReportService.deleteBookReport(1L, 1L);

        assertThatThrownBy(bookShelf::getBookReport).isInstanceOf(
            BookReportNotFoundException.class);
    }

    @Test
    void 서재에_등록된_책_독후감_수정_성공() throws Exception {
        ReviseBookReportRequest reviseBookReportRequest = ReviseBookReportRequest.builder()
            .title("title2")
            .content("content2")
            .build();
        BookReport bookReport = getBookReport();

        bookReport.setCreatedAt(LocalDateTime.now());

        BookShelf bookShelf = BookShelf.builder()
            .bookReport(bookReport)
            .build();

        when(bookShelfRepository.findWithReportByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookReportService.reviseBookReport(1L, 1L, reviseBookReportRequest);

        String result = bookReport.getTitle();
        assertThat(result).isEqualTo("title2");
    }
}