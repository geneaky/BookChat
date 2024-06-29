package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.WISH;
import static toy.bookchat.bookchat.domain.bookshelf.Star.FIVE;
import static toy.bookchat.bookchat.domain.bookshelf.Star.HALF;
import static toy.bookchat.bookchat.domain.bookshelf.Star.THREE;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.BookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.user.service.UserReader;

@ExtendWith(MockitoExtension.class)
class BookShelfServiceTest {

    @Mock
    private BookShelfManager bookShelfManager;
    @Mock
    private BookShelfReader bookShelfReader;
    @Mock
    private BookReader bookReader;
    @Mock
    private UserReader userReader;
    @InjectMocks
    private BookShelfService bookShelfService;

    private BookEntity getBook() {
        return BookEntity.builder()
            .id(1L)
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publishAt(LocalDate.now())
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .build();
    }

    private UserEntity getUser() {
        return UserEntity.builder()
            .build();
    }

    private BookRequest getBookRequest() {
        return BookRequest.builder()
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .build();
    }

    private BookShelfRequest getBookShelfRequest(ReadingStatus readingStatus) {
        if (readingStatus == COMPLETE) {
            return BookShelfRequest.builder()
                .bookRequest(getBookRequest())
                .readingStatus(COMPLETE)
                .star(THREE)
                .build();
        }
        return BookShelfRequest.builder()
            .bookRequest(getBookRequest())
            .readingStatus(READING)
            .build();
    }

    @Test
    void 서재에서_책_조회_성공() throws Exception {
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .id(1L)
            .bookEntity(getBook())
            .readingStatus(WISH)
            .pages(0)
            .star(null)
            .build();

        given(bookShelfReader.readBookShelf(1L, 1L)).willReturn(bookShelfEntity);

        BookShelfResponse result = bookShelfService.getBookOnBookShelf(1L, 1L);
        BookShelfResponse expect = BookShelfResponse.from(bookShelfEntity);
        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 서재에_책_저장_성공() throws Exception {
        BookShelfRequest bookShelfRequest = getBookShelfRequest(COMPLETE);

        bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);

        verify(bookShelfManager).store(any(BookShelfEntity.class));
    }

    @Test
    void 도서_상태에따라_서재에서_조회_성공() throws Exception {
        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = getUser();
        BookEntity bookEntity = BookEntity.builder()
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .userEntity(userEntity)
            .bookEntity(bookEntity)
            .readingStatus(WISH)
            .pages(0)
            .star(null)
            .build();

        Page<BookShelfEntity> bookShelves = new PageImpl<>(List.of(bookShelfEntity), pageable, 1);
        when(bookShelfReader.readBookShelf(userEntity.getId(), WISH, pageable)).thenReturn(bookShelves);

        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = bookShelfService.takeBooksOutOfBookShelves(
            WISH, pageable, userEntity.getId());

        assertThat(searchBookShelfByReadingStatus.getContents().get(0).getIsbn()).isEqualTo("1234");
    }

    @Test
    void 책이_서재에_등록되어있다면_응답성공() throws Exception {
        BookEntity bookEntity = getBook();
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .id(1L)
            .bookEntity(bookEntity)
            .readingStatus(WISH)
            .build();

        when(bookShelfReader.readBookShelf(anyLong(), anyString(), any(LocalDate.class))).thenReturn(bookShelfEntity);

        ExistenceBookOnBookShelfResponse result = bookShelfService.getBookIfExisted(bookEntity.getIsbn(), bookEntity.getPublishAt(), bookEntity.getId());

        ExistenceBookOnBookShelfResponse expect = ExistenceBookOnBookShelfResponse.from(bookShelfEntity);

        assertThat(result).usingRecursiveComparison().isEqualTo(expect);
    }

    @Test
    void 읽고있는_책_현재쪽수_업데이트_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(123)
            .star(null)
            .readingStatus(READING)
            .build();

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .readingStatus(READING)
            .pages(0)
            .star(null)
            .build();

        when(bookShelfReader.readBookShelf(any(), any())).thenReturn(bookShelfEntity);

        bookShelfService.reviseBookShelf(1L, reviseBookShelfRequest, 1L);

        Integer result = bookShelfEntity.getPages();
        assertThat(result).isEqualTo(123);
    }

    @Test
    void 책장에_책_독서상태_변경_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(123)
            .star(null)
            .readingStatus(READING)
            .build();

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .readingStatus(WISH)
            .build();

        when(bookShelfReader.readBookShelf(any(), any())).thenReturn(bookShelfEntity);

        bookShelfService.reviseBookShelf(1L, reviseBookShelfRequest, 1L);

        ReadingStatus readingStatus = bookShelfEntity.getReadingStatus();
        assertThat(readingStatus).isEqualTo(READING);
    }

    @Test
    void 독서완료_서재_별점수정_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(123)
            .star(FIVE)
            .readingStatus(COMPLETE)
            .build();

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .star(HALF)
            .readingStatus(COMPLETE)
            .build();

        when(bookShelfReader.readBookShelf(any(), any())).thenReturn(bookShelfEntity);
        bookShelfService.reviseBookShelf(1L, reviseBookShelfRequest, 1L);

        Star result = bookShelfEntity.getStar();
        assertThat(result).isEqualTo(FIVE);
    }

    @Test
    void 책장에서_책_삭제_성공() throws Exception {
        bookShelfService.deleteBookShelf(1L, 1L);

        verify(bookShelfManager).vacate(any(), any());
    }


    @Test
    void 사용자의_서재_비우기_성공() throws Exception {
        bookShelfService.deleteAllUserBookShelves(any());

        verify(bookShelfManager).remove(any());
    }
}
