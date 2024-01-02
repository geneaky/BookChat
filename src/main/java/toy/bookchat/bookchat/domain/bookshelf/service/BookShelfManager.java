package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookReportRepository;

@Component
public class BookShelfManager {

    private final BookReportRepository bookReportRepository;

    public BookShelfManager(BookReportRepository bookReportRepository) {
        this.bookReportRepository = bookReportRepository;
    }

    @Transactional
    public void append(BookShelf bookShelf, BookReport bookReport) {
        bookReportRepository.save(bookReport);
        bookShelf.writeReportInStateOfCompleteReading(bookReport);
    }
}
