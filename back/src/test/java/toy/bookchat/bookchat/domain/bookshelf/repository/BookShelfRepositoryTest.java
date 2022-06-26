package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class BookShelfRepositoryTest {

    @Autowired
    private BookShelfRepository bookShelfRepository;

    @Test
    public void 책_저장() throws Exception {

//        bookShelfRepository.save()
    }
}
