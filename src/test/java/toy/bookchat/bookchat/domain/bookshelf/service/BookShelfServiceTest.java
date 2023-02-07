package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookShelfServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    BookShelfRepository bookShelfRepository;
    @Mock
    AgonyRepository agonyRepository;
    @Mock
    AgonyRecordRepository agonyRecordRepository;
    @InjectMocks
    BookShelfService bookShelfService;

    private Book getBook() {
        return Book.builder()
            .id(1L)
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publishAt(LocalDate.now())
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .build();
    }

    private User getUser() {
        return User.builder()
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
    void 내부에_등록된_책을_책장에_저장() throws Exception {
        BookShelfRequest bookShelfRequest = getBookShelfRequest(READING);
        Book book = getBook();

        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(
            Optional.of(book));
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);

        verify(bookShelfRepository).save(any(BookShelf.class));
    }

    @Test
    void 내부에_등록되지_않은_책을_책장에_저장() throws Exception {
        BookShelfRequest bookShelfRequest = getBookShelfRequest(READING);

        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));
        bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);

        verify(bookRepository).save(any(Book.class));
        verify(bookShelfRepository).save(any(BookShelf.class));
    }

    @Test
    void 읽은_책_저장_성공() throws Exception {
        BookShelfRequest bookShelfRequest = getBookShelfRequest(COMPLETE);
        Book book = getBook();

        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(
            Optional.of(book));
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);

        verify(bookShelfRepository).save(any(BookShelf.class));
    }

    @Test
    void 읽은_책_저장시_평점_없으면_예외발생() throws Exception {
        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .bookRequest(getBookRequest())
            .readingStatus(COMPLETE)
            .build();

        when(bookRepository.findByIsbnAndPublishAt(any(), any())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        assertThatThrownBy(() -> {
            bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 읽고있는_책을_조회_성공() throws Exception {
        Pageable pageable = mock(Pageable.class);
        User user = getUser();
        Book book = Book.builder()
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .readingStatus(READING)
            .pages(252)
            .star(null)
            .build();

        Page<BookShelf> bookShelves = new PageImpl<>(List.of(bookShelf), pageable, 1);

        when(bookShelfRepository.findSpecificStatusBookByUserId(any(), any(), any())).thenReturn(
            bookShelves);
        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = bookShelfService.takeBooksOutOfBookShelves(
            READING, pageable, 1L);

        verify(bookShelfRepository).findSpecificStatusBookByUserId(READING, pageable, 1L);

        assertThat(searchBookShelfByReadingStatus.getContents().get(0).getIsbn()).isEqualTo(
            "1234");
    }

    @Test
    void 읽은_책을_조회_성공() throws Exception {
        Pageable pageable = mock(Pageable.class);
        User user = getUser();
        Book book = Book.builder()
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .readingStatus(COMPLETE)
            .pages(0)
            .star(FIVE)
            .build();

        Page<BookShelf> bookShelves = new PageImpl<>(List.of(bookShelf), pageable, 1);

        when(bookShelfRepository.findSpecificStatusBookByUserId(any(), any(), any())).thenReturn(
            bookShelves);

        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = bookShelfService.takeBooksOutOfBookShelves(
            COMPLETE, pageable, user.getId());

        verify(bookShelfRepository).findSpecificStatusBookByUserId(COMPLETE, pageable,
            user.getId());

        assertThat(searchBookShelfByReadingStatus.getContents().get(0).getIsbn()).isEqualTo(
            "1234");
    }

    @Test
    void 읽을_책을_조회_성공() throws Exception {
        Pageable pageable = mock(Pageable.class);
        User user = getUser();
        Book book = Book.builder()
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();
        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .readingStatus(WISH)
            .pages(0)
            .star(null)
            .build();

        Page<BookShelf> bookShelves = new PageImpl<>(List.of(bookShelf), pageable, 1);
        when(bookShelfRepository.findSpecificStatusBookByUserId(any(), any(), any())).thenReturn(
            bookShelves);

        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = bookShelfService.takeBooksOutOfBookShelves(
            WISH, pageable, user.getId());

        verify(bookShelfRepository).findSpecificStatusBookByUserId(WISH, pageable, user.getId());

        assertThat(searchBookShelfByReadingStatus.getContents().get(0).getIsbn()).isEqualTo(
            "1234");
    }

    @Test
    void 읽고있는_책_현재쪽수_업데이트_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(Optional.of(123))
            .star(null)
            .readingStatus(READING)
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(READING)
            .pages(0)
            .star(null)
            .build();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookShelfService.reviseBookShelf(1L, reviseBookShelfRequest, 1L);

        Integer result = bookShelf.getPages();
        assertThat(result).isEqualTo(123);
    }

    @Test
    void 책장에서_책_삭제_성공() throws Exception {
        bookShelfService.deleteBookShelf(1L, 1L);

        verify(agonyRecordRepository).deleteByBookShelfIdAndUserId(any(), any());
        verify(agonyRepository).deleteByBookShelfIdAndUserId(any(), any());
        verify(bookShelfRepository).deleteBookShelfByIdAndUserId(any(), any());
    }

    @Test
    void 책장에_책_독서상태_변경_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(Optional.of(123))
            .star(null)
            .readingStatus(READING)
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .readingStatus(WISH)
            .build();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookShelfService.reviseBookShelf(1L, reviseBookShelfRequest, 1L);

        ReadingStatus readingStatus = bookShelf.getReadingStatus();
        assertThat(readingStatus).isEqualTo(READING);
    }

    @Test
    void 서재에_등록하지_않은_책_상태변경시도시_예외발생() throws Exception {
        assertThatThrownBy(() -> {
            bookShelfService.reviseBookShelf(1L, mock(ReviseBookShelfRequest.class), 1L);
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 독서완료_서재_별점수정_성공() throws Exception {
        ReviseBookShelfRequest reviseBookShelfRequest = ReviseBookShelfRequest.builder()
            .pages(Optional.of(123))
            .star(Optional.of(FIVE))
            .readingStatus(COMPLETE)
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .star(HALF)
            .readingStatus(COMPLETE)
            .build();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelf));
        bookShelfService.reviseBookShelf(1L, reviseBookShelfRequest, 1L);

        Star result = bookShelf.getStar();
        assertThat(result).isEqualTo(FIVE);
    }

    @Test
    void 사용자의_서재_비우기_성공() throws Exception {
        bookShelfService.deleteAllUserBookShelves(any());

        verify(bookShelfRepository).deleteAllByUserId(any());
    }

    @Test
    void 책이_서재에_등록되어있다면_응답성공() throws Exception {
        Book book = getBook();
        BookShelf bookShelf = BookShelf.builder()
            .id(1L)
            .book(book)
            .readingStatus(WISH)
            .build();

        when(bookShelfRepository.findByUserIdAndIsbnAndPublishAt(any(), any(), any())).thenReturn(
            Optional.of(bookShelf));

        ExistenceBookOnBookShelfResponse result = bookShelfService.getBookIfExisted(
            book.getIsbn(), book.getPublishAt(), book.getId());

        ExistenceBookOnBookShelfResponse expect = ExistenceBookOnBookShelfResponse.from(bookShelf);

        assertThat(result).usingRecursiveComparison().isEqualTo(expect);
    }

    @Test
    void 책이_서재에_등록되어있지않다면_예외발생() throws Exception {
        Book book = getBook();

        assertThatThrownBy(() -> {
            ExistenceBookOnBookShelfResponse result = bookShelfService.getBookIfExisted(
                book.getIsbn(), book.getPublishAt(), book.getId());
        }).isInstanceOf(BookNotFoundException.class);
    }
}
