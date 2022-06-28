package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class BookShelfRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void 책_저장() throws Exception {
        Book book = Book.builder()
                .isbn("1234")
                .title("effective java")
                .authors(List.of("Joshua"))
                .publisher("insight")
                .bookCoverImageUrl("bookCover@naver.com")
                .build();

        Book savedBook = bookRepository.save(book);
        assertThat(book).isEqualTo(savedBook);
    }
}
