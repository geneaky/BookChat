package toy.bookchat.bookchat.domain.book.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.book.Book;

@ExtendWith(MockitoExtension.class)
class BookReaderTest {

    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookReader bookReader;

    @Test
    void isbn_출판일과_일치하는_책이있다면_조회한다() throws Exception {
        BookEntity bookEntity = BookEntity.builder().build();
        given(bookRepository.findByIsbnAndPublishAt(any(), any())).willReturn(Optional.of(bookEntity));

        Book book = Book.builder().build();
        bookReader.readBook(book);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void isbn_출판일과_일치하는_책이없다면_신규등록후_반환한다() throws Exception {
        given(bookRepository.save(any())).willReturn(BookEntity.builder().build());
        Book book = Book.builder().build();
        bookReader.readBook(book);

        verify(bookRepository).save(any());
    }
}