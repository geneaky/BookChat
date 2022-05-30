package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.book.model.Book;
import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@ExtendWith(MockitoExtension.class)
public class BookShelfServiceTest {

    @Mock
    BookShelfRepository bookShelfRepository;
    @InjectMocks
    BookShelfServiceImpl bookShelfService;

    @Test
    public void 책_저장() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = spy(BookShelfRequestDto.class);

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto);

        verify(bookShelfRepository).save(any(Book.class));
    }
}
