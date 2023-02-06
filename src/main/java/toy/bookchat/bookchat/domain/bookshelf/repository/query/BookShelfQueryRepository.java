package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

public interface BookShelfQueryRepository {

    Page<BookShelf> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId);

    void deleteBookByUserIdAndBookId(Long userId, Long bookId);

    Optional<BookShelf> findByUserIdAndBookId(Long userId, Long bookId);

    void deleteAllByUserId(Long userId);

    Optional<BookShelf> findByUserIdAndIsbnAndPublishAt(Long userId, String isbn,
        LocalDate publishAt);

    Optional<BookShelf> findByIdAndUserId(Long bookShelfId, Long userId);
}
