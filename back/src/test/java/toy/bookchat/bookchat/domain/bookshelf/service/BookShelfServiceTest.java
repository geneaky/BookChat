package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.book.model.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@ExtendWith(MockitoExtension.class)
public class BookShelfServiceTest {

    @Mock
    BookShelfRepository bookShelfRepository;
    @Mock
    BookRepository bookRepository;
    @InjectMocks
    BookShelfServiceImpl bookShelfService;

    private Book getBook() {
        return Book.builder()
            .isbn("12345")
            .title("testBook")
            .authors(List.of("test Author"))
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .build();
    }

    private BookShelfRequestDto getBookShelfRequestDto() {
        return BookShelfRequestDto.builder()
            .isbn("12345")
            .title("testBook")
            .author(List.of("test Author"))
            .publisher("test publisher")
            .bookCoverImageUrl("test@naver.com")
            .readingStatus(ReadingStatus.READING)
            .build();
    }

    @Test
    public void 내부에_등록된_책을_책장에_저장() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = getBookShelfRequestDto();
        Long userId = 1L;
        Book book = getBook();

        when(bookRepository.findByIsbn(bookShelfRequestDto.getIsbn())).thenReturn(
            Optional.of(book));

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto, userId);

        verify(bookShelfRepository).save(any(BookShelf.class));
    }


    @Test
    public void 내부에_등록되지_않은_책을_책장에_저장() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = getBookShelfRequestDto();
        Long userId = 1L;

        when(bookRepository.findByIsbn(bookShelfRequestDto.getIsbn())).thenReturn(Optional.empty());

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto, userId);

        verify(bookRepository).save(any(Book.class));
        verify(bookShelfRepository).save(any(BookShelf.class));

    }

}
