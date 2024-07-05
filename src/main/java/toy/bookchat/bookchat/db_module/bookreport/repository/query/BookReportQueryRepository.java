package toy.bookchat.bookchat.db_module.bookreport.repository.query;

import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;

public interface BookReportQueryRepository {

    BookReportEntity findByUserIdAndBookShelfId(Long userId, Long bookShelfId);
}
