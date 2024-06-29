package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.WriteBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.BookReportResponse;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.exception.notfound.bookshelf.BookReportNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookReportServiceTest {

    @Mock
    private BookShelfManager bookShelfManager;
    @Mock
    private BookShelfReader bookShelfReader;
    @InjectMocks
    private BookReportService bookReportService;

    private static WriteBookReportRequest getWriteBookReportRequest() {
        return WriteBookReportRequest.builder()
            .title("어렵지만 많이 배웠습니다")
            .content("이런이런 저런저런 내용")
            .build();
    }

    private static BookReportEntity getBookReport() {
        return BookReportEntity.builder()
            .title("title")
            .content("content")
            .build();
    }

    @Test
    void 독후감_등록_성공() throws Exception {
        WriteBookReportRequest writeBookReportRequest = getWriteBookReportRequest();

        UserEntity userEntity = mock(UserEntity.class);
        BookShelfEntity bookShelfEntity = mock(BookShelfEntity.class);

        when(userEntity.getId()).thenReturn(1L);
        when(bookShelfReader.readBookShelf(any(), any())).thenReturn(bookShelfEntity);

        bookReportService.writeReport(writeBookReportRequest, 1L, userEntity.getId());

        verify(bookShelfManager).append(any(), any());
    }


    @Test
    void 서재에_등록된_책_독후감_조회_성공() throws Exception {
        BookReportEntity bookReportEntity = getBookReport();

        bookReportEntity.setCreatedAt(LocalDateTime.now());

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookReportEntity(bookReportEntity)
            .build();

        when(bookShelfReader.readBookShelf(any(), any())).thenReturn(bookShelfEntity);
        BookReportResponse bookReportResponse = bookReportService.getBookReportResponse(1L, 1L);

        assertThat(bookReportResponse.getReportTitle()).isEqualTo(bookReportEntity.getTitle());
    }

    @Test
    void 서재에_등록된_책_독후감_삭제_성공() throws Exception {
        BookReportEntity bookReportEntity = getBookReport();

        bookReportEntity.setCreatedAt(LocalDateTime.now());

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookReportEntity(bookReportEntity)
            .build();

        when(bookShelfReader.readBookShelf(any(), any())).thenReturn(bookShelfEntity);

        bookReportService.deleteBookReport(1L, 1L);

        assertThatThrownBy(bookShelfEntity::getBookReportEntity).isInstanceOf(
            BookReportNotFoundException.class);
    }

    @Test
    void 서재에_등록된_책_독후감_수정_성공() throws Exception {
        ReviseBookReportRequest reviseBookReportRequest = ReviseBookReportRequest.builder()
            .title("title2")
            .content("content2")
            .build();
        BookReportEntity bookReportEntity = getBookReport();

        bookReportEntity.setCreatedAt(LocalDateTime.now());

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookReportEntity(bookReportEntity)
            .build();

        when(bookShelfReader.readBookShelf(any(), any())).thenReturn(bookShelfEntity);
        bookReportService.reviseBookReport(1L, 1L, reviseBookReportRequest);

        assertThat(bookReportEntity.getTitle()).isEqualTo(reviseBookReportRequest.getTitle());
    }
}