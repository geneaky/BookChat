package toy.bookchat.bookchat.domain.bookshelf.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

public interface BookShelfQueryRepository {

    Page<BookShelf> findSpecificStatusBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, Long userId);

    BookShelf findReadingBookByUserIdAndIsbn(Long userId, String isbn);

    void deleteBookByUserIdAndIsbn(Long userId, String isbn);
}
