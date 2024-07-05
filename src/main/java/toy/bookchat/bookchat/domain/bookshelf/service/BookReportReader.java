package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.exception.notfound.bookshelf.BookReportNotFoundException;

@Component
public class BookReportReader {

    private final BookReportRepository bookReportRepository;

    public BookReportReader(BookReportRepository bookReportRepository) {
        this.bookReportRepository = bookReportRepository;
    }

    public BookReport readBookReport(Long userId, Long bookShelfId) {
        BookReportEntity bookReportEntity = bookReportRepository.findByUserIdAndBookShelfId(userId, bookShelfId);
        if (bookReportEntity == null) {
            throw new BookReportNotFoundException();
        }

        return BookReport.builder()
            .id(bookReportEntity.getId())
            .title(bookReportEntity.getTitle())
            .content(bookReportEntity.getContent())
            .reportedAt(bookReportEntity.getCreatedAt())
            .build();
    }
}
