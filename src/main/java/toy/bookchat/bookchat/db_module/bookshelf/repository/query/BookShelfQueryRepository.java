package toy.bookchat.bookchat.db_module.bookshelf.repository.query;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfWithBook;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

public interface BookShelfQueryRepository {

    Optional<BookShelfEntity> findByIdAndUserId(Long bookShelfId, Long userId);

    void deleteBookShelfByIdAndUserId(Long bookShelfId, Long userId);

    void deleteAllByUserId(Long userId);

    Page<BookShelfWithBook> findBookShelfWithBook(Long userId, ReadingStatus readingStatus, Pageable pageable);

    Optional<BookShelfWithBook> findByUserIdAndIsbnAndPublishAt(Long userId, String isbn, LocalDate publishAt);

    Optional<BookShelfWithBook> findBookShelfWithBook(Long userId, Long bookShelfId);
}
