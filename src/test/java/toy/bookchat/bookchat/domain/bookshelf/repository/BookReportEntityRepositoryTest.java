package toy.bookchat.bookchat.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;

class BookReportEntityRepositoryTest extends RepositoryTest {

    @Autowired
    BookReportRepository bookReportRepository;
    @Autowired
    BookShelfRepository bookShelfRepository;

    private BookReportEntity getBookReport(BookShelfEntity bookShelfEntity) {
        return BookReportEntity.builder()
            .title("title")
            .content("content")
            .bookShelfEntity(bookShelfEntity)
            .build();
    }

    @Test
    void 독후감_저장_성공() throws Exception {
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .build();
        bookShelfRepository.save(bookShelfEntity);

        BookReportEntity bookReportEntity = getBookReport(bookShelfEntity);
        bookReportRepository.save(bookReportEntity);

        BookReportEntity findBookReportEntity = bookReportRepository.findById(bookReportEntity.getId()).get();

        assertThat(findBookReportEntity).isEqualTo(bookReportEntity);
    }
}