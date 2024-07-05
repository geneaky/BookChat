package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookReportTitleAndContent;
import toy.bookchat.bookchat.exception.notfound.bookshelf.BookReportNotFoundException;

@Component
public class BookReportManager {

    private final BookReportRepository bookReportRepository;

    public BookReportManager(BookReportRepository bookReportRepository) {
        this.bookReportRepository = bookReportRepository;
    }

    public void delete(BookReport bookReport) {
        bookReportRepository.deleteById(bookReport.getId());
    }

    public void modify(Long userId, Long bookShelfId, BookReportTitleAndContent bookReportTitleAndContent) {
        BookReportEntity bookReportEntity = bookReportRepository.findByUserIdAndBookShelfId(userId, bookShelfId);
        if (bookReportEntity == null) {
            throw new BookReportNotFoundException();
        }

        bookReportEntity.reviseTitle(bookReportTitleAndContent.getTitle());
        bookReportEntity.reviseContent(bookReportTitleAndContent.getContent());
    }
}
