package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookShelfRepository extends JpaRepository<Book, Long> {

}
