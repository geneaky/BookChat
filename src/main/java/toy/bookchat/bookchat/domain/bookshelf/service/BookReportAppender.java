package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Component
public class BookReportAppender {

    private final BookReportRepository bookReportRepository;

    public BookReportAppender(BookReportRepository bookReportRepository) {
        this.bookReportRepository = bookReportRepository;
    }

    public void append(BookShelf bookShelf, BookReport bookReport) {
        BookReportEntity bookReportEntity = BookReportEntity.builder()
            .title(bookReport.getTitle())
            .content(bookReport.getContent())
            .bookShelfId(bookShelf.getId())
            .build();

        bookReportRepository.save(bookReportEntity);
    }
}
