package repository;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByIsbnAndPublishAt(String isbn, LocalDate publishAt);
}
