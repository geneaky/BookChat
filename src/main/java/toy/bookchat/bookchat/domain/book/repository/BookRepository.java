package toy.bookchat.bookchat.domain.book.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.book.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbnAndPublishAt(String isbn, LocalDate publishAt);
}
