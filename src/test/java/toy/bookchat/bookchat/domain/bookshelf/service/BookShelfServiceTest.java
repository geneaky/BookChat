package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeBookStatusRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeReadingBookPageRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfStarRequest;
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
    @InjectMocks
    BookShelfService bookShelfService;

    private Book getBook() {
        return Book.builder()
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .build();
    }

    private User getUser() {
        return User.builder()
            .build();
    }

    private BookShelfRequest getBookShelfRequest(ReadingStatus readingStatus) {
        if (readingStatus == ReadingStatus.COMPLETE) {
            return BookShelfRequest.builder()
                .isbn("12345")
                .title("testBook")
                .authors(List.of("test Author"))
                .publisher("test publisher")
                .bookCoverImageUrl("test@naver.com")
                .readingStatus(ReadingStatus.COMPLETE)
                .star(Star.THREE)
                .singleLineAssessment("very good")
                .build();
        }
        return BookShelfRequest.builder()
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .readingStatus(ReadingStatus.READING)
            .build();
    }

    @Test
    void 내부에_등록된_책을_책장에_저장() throws Exception {
        BookShelfRequest bookShelfRequest = getBookShelfRequest(ReadingStatus.READING);
        Book book = getBook();

        when(bookRepository.findByIsbn(bookShelfRequest.getIsbn())).thenReturn(
            Optional.of(book));
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);

        verify(bookShelfRepository).save(any(BookShelf.class));
    }


    @Test
    void 내부에_등록되지_않은_책을_책장에_저장() throws Exception {
        BookShelfRequest bookShelfRequest = getBookShelfRequest(ReadingStatus.READING);

        when(bookRepository.findByIsbn(bookShelfRequest.getIsbn())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));
        bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);

        verify(bookRepository).save(any(Book.class));
        verify(bookShelfRepository).save(any(BookShelf.class));
    }

    @Test
    void 읽은_책_저장_성공() throws Exception {
        BookShelfRequest bookShelfRequest = getBookShelfRequest(ReadingStatus.COMPLETE);
        Book book = getBook();

        when(bookRepository.findByIsbn(any())).thenReturn(
            Optional.of(book));
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        bookShelfService.putBookOnBookShelf(bookShelfRequest, 1L);

        verify(bookShelfRepository).save(any(BookShelf.class));
    }

    @Test
    void 읽은_책_저장시_평점과_한줄평이_없으면_예외발생() throws Exception {
        BookShelfRequest bookShelfRequest = BookShelfRequest.builder()
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .readingStatus(ReadingStatus.COMPLETE)
            .build();

        when(bookRepository.findByIsbn(bookShelfRequest.getIsbn())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        User user = getUser();
        Assertions.assertThatThrownBy(() -> {
            bookShelfService.putBookOnBookShelf(bookShelfRequest, user.getId());
        }).isInstanceOf(IllegalArgumentException.class);
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
            .readingStatus(ReadingStatus.READING)
            .pages(252)
            .star(null)
            .singleLineAssessment(null)
            .build();

        Page<BookShelf> bookShelves = new PageImpl<>(List.of(bookShelf), pageable, 1);

        when(bookShelfRepository.findSpecificStatusBookByUserId(any(), any(), any())).thenReturn(
            bookShelves);
        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = bookShelfService.takeBooksOutOfBookShelf(
            ReadingStatus.READING, pageable, user.getId());

        verify(bookShelfRepository).findSpecificStatusBookByUserId(ReadingStatus.READING,
            pageable, user.getId());

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
            .readingStatus(ReadingStatus.COMPLETE)
            .pages(0)
            .star(Star.FIVE)
            .singleLineAssessment("최고")
            .build();

        Page<BookShelf> bookShelves = new PageImpl<>(List.of(bookShelf), pageable, 1);

        when(bookShelfRepository.findSpecificStatusBookByUserId(any(), any(), any())).thenReturn(
            bookShelves);

        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = bookShelfService.takeBooksOutOfBookShelf(
            ReadingStatus.COMPLETE, pageable, user.getId());

        verify(bookShelfRepository).findSpecificStatusBookByUserId(ReadingStatus.COMPLETE,
            pageable, user.getId());

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
            .readingStatus(ReadingStatus.WISH)
            .pages(0)
            .star(null)
            .singleLineAssessment(null)
            .build();

        Page<BookShelf> bookShelves = new PageImpl<>(List.of(bookShelf), pageable, 1);
        when(bookShelfRepository.findSpecificStatusBookByUserId(any(), any(), any())).thenReturn(
            bookShelves);

        SearchBookShelfByReadingStatus searchBookShelfByReadingStatus = bookShelfService.takeBooksOutOfBookShelf(
            ReadingStatus.WISH, pageable, user.getId());

        verify(bookShelfRepository).findSpecificStatusBookByUserId(ReadingStatus.WISH,
            pageable, user.getId());

        assertThat(searchBookShelfByReadingStatus.getContents().get(0).getIsbn()).isEqualTo(
            "1234");
    }

    @Test
    void 읽고있는_책_현재쪽수_업데이트_성공() throws Exception {
        ChangeReadingBookPageRequest changeReadingBookPageRequest = new ChangeReadingBookPageRequest(
            123);

        User user = getUser();

        Book book = Book.builder()
            .id(1L)
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .readingStatus(ReadingStatus.READING)
            .pages(0)
            .star(null)
            .singleLineAssessment(null)
            .build();

        when(bookShelfRepository.findOneOnConditionByUserIdAndBookId(any(), any(),
            any())).thenReturn(
            bookShelf);

        bookShelfService.changeReadingBookPage(changeReadingBookPageRequest, user.getId(),
            book.getId());

        Integer result = bookShelf.getPages();
        assertThat(result).isEqualTo(123);
    }

    @Test
    void 책장에서_책_삭제_성공() throws Exception {

        User user = getUser();

        Book book = Book.builder()
            .id(1L)
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();

        bookShelfService.deleteBookOnBookShelf(book.getId(), user.getId());

        verify(bookShelfRepository).deleteBookByUserIdAndBookId(any(), any());
    }

    @Test
    void 책장에_책_독서상태_변경_성공() throws Exception {
        User user = getUser();

        Book book = Book.builder()
            .id(1L)
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();

        BookShelf bookShelf = BookShelf.builder()
            .user(user)
            .book(book)
            .readingStatus(ReadingStatus.WISH)
            .build();

        when(bookShelfRepository.findByUserIdAndBookId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        bookShelfService.changeBookStatusOnBookShelf(
            new ChangeBookStatusRequest(ReadingStatus.READING), user.getId(), book.getId());

        ReadingStatus readingStatus = bookShelf.getReadingStatus();
        assertThat(readingStatus).isEqualTo(ReadingStatus.READING);
    }

    @Test
    void 서재에_등록하지_않은_책_상태변경시도시_예외발생() throws Exception {
        User user = getUser();

        Book book = Book.builder()
            .id(1L)
            .isbn("1234")
            .title("toby's Spring")
            .authors(List.of("이일민"))
            .publisher("jpub")
            .bookCoverImageUrl("testBookCoverImageUrl")
            .build();

        assertThatThrownBy(() -> {
            bookShelfService.changeBookStatusOnBookShelf(
                new ChangeBookStatusRequest(ReadingStatus.READING), user.getId(), book.getId());
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 독서완료_서재_별점수정_성공() throws Exception {
        BookShelf bookShelf = BookShelf.builder()
            .star(Star.HALF)
            .build();
        ReviseBookShelfStarRequest reviseBookShelfStarRequest = ReviseBookShelfStarRequest.of(
            Star.FIVE);

        when(bookShelfRepository.findOneOnConditionByUserIdAndBookId(1L, 1L,
            ReadingStatus.COMPLETE)).thenReturn(bookShelf);
        bookShelfService.reviseBookStar(1L, 1L, reviseBookShelfStarRequest);

        Star result = bookShelf.getStar();
        assertThat(result).isEqualTo(Star.FIVE);
    }
}
