package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {

}
