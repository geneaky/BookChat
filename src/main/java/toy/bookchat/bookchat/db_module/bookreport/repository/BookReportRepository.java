package toy.bookchat.bookchat.db_module.bookreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookreport.repository.query.BookReportQueryRepository;

public interface BookReportRepository extends BookReportQueryRepository, JpaRepository<BookReportEntity, Long> {

}
