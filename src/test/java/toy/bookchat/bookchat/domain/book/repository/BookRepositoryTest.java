package toy.bookchat.bookchat.domain.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.Book;

@RepositoryTest
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    void ISBN_출판일로_도서_조회_성공() throws Exception {
        String isbn = "1234567890";
        LocalDate publishAt = LocalDate.now();
        Book book = Book.builder()
            .isbn(isbn)
            .publishAt(publishAt)
            .build();

        bookRepository.save(book);

        Book findBook = bookRepository.findByIsbnAndPublishAt(isbn, publishAt).get();

        assertThat(findBook).isEqualTo(book);
    }
}