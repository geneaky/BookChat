package toy.bookchat.bookchat.domain.bookshelf.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfSearchResponseDto;
import toy.bookchat.bookchat.domain.user.User;

public interface BookShelfRepository extends JpaRepository<BookShelf, Long> {

    List<BookShelfSearchResponseDto> findSpecificReadingStateBookByUserId(
        ReadingStatus readingStatus, Pageable pageable, User user);
}
