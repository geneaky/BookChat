package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.WISH;
import static toy.bookchat.bookchat.domain.bookshelf.Star.FIVE;
import static toy.bookchat.bookchat.domain.bookshelf.Star.FOUR;
import static toy.bookchat.bookchat.domain.bookshelf.Star.ONE_HALF;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfPageAndStarAndReadingStatus;
import toy.bookchat.bookchat.domain.user.service.UserReader;

@ExtendWith(MockitoExtension.class)
class BookShelfServiceTest {

    @Mock
    private BookShelfManager bookShelfManager;
    @Mock
    private BookShelfReader bookShelfReader;
    @Mock
    private BookShelfAppender bookShelfAppender;
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

    @Test
    void 서재에서_책_조회_성공() throws Exception {
        bookShelfService.getBookOnBookShelf(1L, 1L);
        verify(bookShelfReader).readBookShelf(any(), any());
    }

    @Test
    void 서재에_책_저장_성공() throws Exception {
        BookShelf bookShelf = BookShelf.builder().build();
        Book book = Book.builder().build();

        bookShelfService.putBookOnBookShelf(bookShelf, book, 1L);

        verify(bookReader).readBook(any());
        verify(userReader).readUser(any());
        verify(bookShelfAppender).append(any(), any(), any());
    }

    @Test
    void 도서_상태에따라_서재에서_조회_성공() throws Exception {
        bookShelfService.takeBooksOutOfBookShelves(WISH, mock(Pageable.class), 1L);

        verify(bookShelfReader).readPagedBookShelves(any(), any(), any());
    }

    @Test
    void 책이_서재에_등록되어있다면_응답성공() throws Exception {
        bookShelfService.getBookIfExisted("1234", LocalDate.now(), 1L);
        verify(bookShelfReader).readBookShelf(any(), anyString(), any());
    }

    @Test
    void 읽고있는_책_현재쪽수_업데이트_성공() throws Exception {
        BookShelfPageAndStarAndReadingStatus bookShelfPageAndStarAndReadingStatus = BookShelfPageAndStarAndReadingStatus.builder()
            .pages(123)
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(READING)
            .build();
        given(bookShelfReader.readBookShelf(any(), any())).willReturn(bookShelf);

        bookShelfService.reviseBookShelf(1L, bookShelfPageAndStarAndReadingStatus, 1L);

        assertThat(bookShelf.getPages()).isEqualTo(123);
    }

    @Test
    void 읽고있는_책_현재쪽수_독서상태_별점_상태_변경_성공() throws Exception {
        BookShelfPageAndStarAndReadingStatus bookShelfPageAndStarAndReadingStatus = BookShelfPageAndStarAndReadingStatus.builder()
            .pages(123)
            .readingStatus(COMPLETE)
            .star(ONE_HALF)
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(READING)
            .build();
        given(bookShelfReader.readBookShelf(any(), any())).willReturn(bookShelf);

        bookShelfService.reviseBookShelf(1L, bookShelfPageAndStarAndReadingStatus, 1L);

        assertAll(
            () -> assertThat(bookShelf.getPages()).isEqualTo(123),
            () -> assertThat(bookShelf.getReadingStatus()).isEqualTo(COMPLETE),
            () -> assertThat(bookShelf.getStar()).isEqualTo(ONE_HALF)
        );
    }

    @Test
    void 독서예정_도서_독서중으로_변경_성공() throws Exception {
        BookShelfPageAndStarAndReadingStatus bookShelfPageAndStarAndReadingStatus = BookShelfPageAndStarAndReadingStatus.builder()
            .readingStatus(READING)
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(WISH)
            .build();
        given(bookShelfReader.readBookShelf(any(), any())).willReturn(bookShelf);

        bookShelfService.reviseBookShelf(1L, bookShelfPageAndStarAndReadingStatus, 1L);

        assertThat(bookShelf.getReadingStatus()).isEqualTo(READING);
    }

    @Test
    void 독서완료_서재_별점수정_성공() throws Exception {
        BookShelfPageAndStarAndReadingStatus bookShelfPageAndStarAndReadingStatus = BookShelfPageAndStarAndReadingStatus.builder()
            .star(FIVE)
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(COMPLETE)
            .star(FOUR)
            .build();
        given(bookShelfReader.readBookShelf(any(), any())).willReturn(bookShelf);

        bookShelfService.reviseBookShelf(1L, bookShelfPageAndStarAndReadingStatus, 1L);

        assertThat(bookShelf.getStar()).isEqualTo(FIVE);
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
