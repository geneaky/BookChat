package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.user.User;

@ExtendWith(MockitoExtension.class)
public class BookShelfServiceTest {

    @Mock
    BookRepository bookRepository;
    @Mock
    BookShelfRepository bookShelfRepository;
    @InjectMocks
    BookShelfService bookShelfService;

    private Book getBook() {
        return new Book("12345", "testBook", List.of("test Author"), "test publisher",
            "test@naver.com");
    }

    private User getUser() {
        return User.builder()
            .build();
    }

    private BookShelfRequestDto getBookShelfRequestDto() {
        return BookShelfRequestDto.builder()
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .readingStatus(ReadingStatus.READING)
            .build();
    }

    @Test
    public void 내부에_등록된_책을_책장에_저장() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = getBookShelfRequestDto();
        Book book = getBook();

        when(bookRepository.findByIsbn(bookShelfRequestDto.getIsbn())).thenReturn(
            Optional.of(book));

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto, getUser());

        verify(bookShelfRepository).save(any(BookShelf.class));
    }


    @Test
    public void 내부에_등록되지_않은_책을_책장에_저장() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = getBookShelfRequestDto();

        when(bookRepository.findByIsbn(bookShelfRequestDto.getIsbn())).thenReturn(Optional.empty());

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto, getUser());

        verify(bookRepository).save(any(Book.class));
        verify(bookShelfRepository).save(any(BookShelf.class));
    }

    @Test
    public void 읽고있는_책을_조회_성공() throws Exception {

        Pageable pageable = mock(Pageable.class);
        User user = getUser();

        bookShelfService.takeBooksOutOfBookShelf(ReadingStatus.READING, pageable,
            user);

        verify(bookShelfRepository).findSpecificStatusBookByUserId(ReadingStatus.READING,
            pageable, user.getId());
    }

    @Test
    public void 읽은_책을_조회_성공() throws Exception {
        Pageable pageable = mock(Pageable.class);
        User user = getUser();

        bookShelfService.takeBooksOutOfBookShelf(ReadingStatus.COMPLETE, pageable,
            user);

        verify(bookShelfRepository).findSpecificStatusBookByUserId(ReadingStatus.COMPLETE,
            pageable, user.getId());
    }

    @Test
    public void 읽을_책을_조회_성공() throws Exception {
        Pageable pageable = mock(Pageable.class);
        User user = getUser();

        bookShelfService.takeBooksOutOfBookShelf(ReadingStatus.WISH, pageable,
            user);

        verify(bookShelfRepository).findSpecificStatusBookByUserId(ReadingStatus.WISH,
            pageable, user.getId());

    }
}
