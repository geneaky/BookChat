package toy.bookchat.bookchat.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookReaderTest {

    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookReader bookReader;

    @Test
    void isbn_출판일과_일치하는_책이있다면_조회한다() throws Exception {
        Book book = Book.builder().build();
        given(bookRepository.findByIsbnAndPublishAt(any(), any())).willReturn(Optional.of(book));
        Book readBook = bookReader.readBook("G5J5X8U", LocalDate.now(), null);

        assertThat(readBook).isEqualTo(book);
    }

    @Test
    void isbn_출판일과_일치하는_책이없다면_신규등록후_반환한다() throws Exception {
        Book book = Book.builder().build();
        given(bookRepository.save(any())).willReturn(book);
        Book readBook = bookReader.readBook("G5J5X8U", LocalDate.now(), null);

        assertThat(readBook).isEqualTo(book);
    }
}