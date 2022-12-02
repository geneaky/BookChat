package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.query.BookShelfQueryRepository;

public interface BookShelfRepository extends BookShelfQueryRepository,
    JpaRepository<BookShelf, Long> {

}
