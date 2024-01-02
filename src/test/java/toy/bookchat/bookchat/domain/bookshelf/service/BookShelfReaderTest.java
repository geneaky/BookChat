package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookShelfReaderTest {

    @Mock
    private BookShelfRepository bookShelfRepository;
    @InjectMocks
    private BookShelfReader bookShelfReader;

    @Test
    void 사용자의_책장_조회시_일치하는_책장이없다면_예외_발생() throws Exception {
        assertThatThrownBy(() -> bookShelfReader.readBookShelf(488L, 897L))
            .isInstanceOf(BookNotFoundException.class);
    }
}