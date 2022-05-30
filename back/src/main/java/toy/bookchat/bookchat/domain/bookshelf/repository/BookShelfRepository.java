package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.book.model.Book;

public interface BookShelfRepository extends JpaRepository<Book, Long> {

}
