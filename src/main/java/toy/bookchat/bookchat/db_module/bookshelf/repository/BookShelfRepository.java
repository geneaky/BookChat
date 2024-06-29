package toy.bookchat.bookchat.db_module.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.query.BookShelfQueryRepository;

public interface BookShelfRepository extends BookShelfQueryRepository,
    JpaRepository<BookShelfEntity, Long> {

}
