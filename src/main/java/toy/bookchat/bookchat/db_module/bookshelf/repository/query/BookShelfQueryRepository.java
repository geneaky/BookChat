package toy.bookchat.bookchat.db_module.bookshelf.repository.query;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

public interface BookShelfQueryRepository {

    Page<BookShelfEntity> findSpecificStatusBookByUserId(ReadingStatus readingStatus, Pageable pageable,
        Long userId);

    Optional<BookShelfEntity> findByUserIdAndIsbnAndPublishAt(Long userId, String isbn,
        LocalDate publishAt);

    Optional<BookShelfEntity> findByIdAndUserId(Long bookShelfId, Long userId);

    Optional<BookShelfEntity> findWithReportByIdAndUserId(Long bookShelfId, Long userId);

    void deleteBookShelfByIdAndUserId(Long bookShelfId, Long userId);

    void deleteAllByUserId(Long userId);
}
