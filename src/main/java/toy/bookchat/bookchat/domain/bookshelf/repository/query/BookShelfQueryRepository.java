package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import java.util.List;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

public interface BookShelfQueryRepository {

    List<BookShelf> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId);
}
