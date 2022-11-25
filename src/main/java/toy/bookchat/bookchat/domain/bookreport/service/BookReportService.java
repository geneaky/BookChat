package toy.bookchat.bookchat.domain.bookreport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookreport.BookReport;
import toy.bookchat.bookchat.domain.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.ReviseBookReportRequest;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.WriteBookReportRequest;
import toy.bookchat.bookchat.domain.bookreport.service.dto.response.BookReportResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@Service
public class BookReportService {

    private final BookReportRepository bookReportRepository;
    private final BookShelfRepository bookShelfRepository;

    public BookReportService(BookReportRepository bookReportRepository,
        BookShelfRepository bookShelfRepository) {
        this.bookReportRepository = bookReportRepository;
        this.bookShelfRepository = bookShelfRepository;
    }

    @Transactional
    public void writeReport(WriteBookReportRequest writeBookReportRequest, Long bookId,
        Long userId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId, bookId)
            .orElseThrow(BookNotFoundException::new);

        bookShelf.changeToCompleteReading();
        BookReport bookReport = writeBookReportRequest.getBookReport(bookShelf);
        bookReportRepository.save(bookReport);
    }

    @Transactional(readOnly = true)
    public BookReportResponse getBookReportResponse(Long bookId, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId, bookId)
            .orElseThrow(BookNotFoundException::new);

        return BookReportResponse.from(bookShelf.getBookReport());
    }

    @Transactional
    public void deleteBookReport(Long bookId, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId, bookId)
            .orElseThrow(BookNotFoundException::new);

        bookShelf.deleteBookReport();
    }

    public void reviseBookReport(Long bookId, Long userId,
        ReviseBookReportRequest reviseBookReportRequest) {
        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId, bookId)
            .orElseThrow(BookNotFoundException::new);
        BookReport bookReport = bookShelf.getBookReport();

        bookReport.reviseTitle(reviseBookReportRequest.getTitle());
        bookReport.reviseContent(reviseBookReportRequest.getContent());
    }
}
