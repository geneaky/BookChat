package toy.bookchat.bookchat.domain.bookreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.bookreport.BookReport;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {

}
