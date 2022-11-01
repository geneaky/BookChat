package toy.bookchat.bookchat.domain.bookreport.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.bookreport.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.configuration.TestConfig;

@DataJpaTest
@Import({JpaAuditingConfig.class, TestConfig.class})
class BookReportRepositoryTest {

    @Autowired
    BookReportRepository bookReportRepository;
    @Autowired
    BookShelfRepository bookShelfRepository;

    private BookReport getBookReport(BookShelf bookShelf) {
        return BookReport.builder()
            .title("title")
            .content("content")
            .bookShelf(bookShelf)
            .build();
    }

    @Test
    void 독후감_저장_성공() throws Exception {
        BookShelf bookShelf = BookShelf.builder()
            .build();
        bookShelfRepository.save(bookShelf);

        BookReport bookReport = getBookReport(bookShelf);
        bookReportRepository.save(bookReport);

        bookReportRepository.flush();
        bookShelfRepository.flush();

        BookReport findBookReport = bookReportRepository.findById(bookReport.getId()).get();

        assertThat(findBookReport).isEqualTo(bookReport);
    }
}