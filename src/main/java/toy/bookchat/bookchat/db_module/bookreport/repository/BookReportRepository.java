package toy.bookchat.bookchat.db_module.bookreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;

public interface BookReportRepository extends JpaRepository<BookReportEntity, Long> {

}
