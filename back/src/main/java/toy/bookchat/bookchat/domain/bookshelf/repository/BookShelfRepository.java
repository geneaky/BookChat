package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

public interface BookShelfRepository extends JpaRepository<BookShelf, Long> {

}
