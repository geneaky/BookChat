package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.print.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@ExtendWith(MockitoExtension.class)
public class BookShelfServiceTest {

    @Mock
    BookShelfServiceImpl bookShelfService;
    @Mock
    BookShelfRepository bookShelfRepository;

    @Test
    public void 읽고있는_책_저장() throws Exception {
        BookShelfRequestDto bookShelfRequestDto = mock(BookShelfRequestDto.class);

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto);

        verify(bookShelfRepository).save(any(Book.class));

    }
}
