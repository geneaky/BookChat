package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReportEntity;

public interface BookReportRepository extends JpaRepository<BookReportEntity, Long> {

}
