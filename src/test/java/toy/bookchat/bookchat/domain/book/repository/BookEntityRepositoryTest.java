package toy.bookchat.bookchat.domain.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;

class BookEntityRepositoryTest extends RepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    void ISBN_출판일로_도서_조회_성공() throws Exception {
        String isbn = "1234567890";
        LocalDate publishAt = LocalDate.now();
        BookEntity bookEntity = BookEntity.builder()
            .isbn(isbn)
            .publishAt(publishAt)
            .build();

        bookRepository.save(bookEntity);

        BookEntity findBookEntity = bookRepository.findByIsbnAndPublishAt(isbn, publishAt).get();

        assertThat(findBookEntity).isEqualTo(bookEntity);
    }

    @Test
    void ISBN과_출판일이_동일한_도서는_중복_저장_실패() throws Exception {
        String isbn = "1234567890";
        LocalDate publishAt = LocalDate.of(2024, 5, 15);
        BookEntity bookEntity1 = BookEntity.builder()
            .isbn(isbn)
            .publishAt(publishAt)
            .build();

        bookRepository.save(bookEntity1);

        BookEntity bookEntity2 = BookEntity.builder()
            .isbn(isbn)
            .publishAt(publishAt)
            .build();

        assertThatThrownBy(() -> bookRepository.save(bookEntity2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}