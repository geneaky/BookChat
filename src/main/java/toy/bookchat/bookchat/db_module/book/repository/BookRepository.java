package toy.bookchat.bookchat.db_module.book.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.book.BookEntity;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByIsbnAndPublishAt(String isbn, LocalDate publishAt);
}
