package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.WriteBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.BookReportResponse;
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
    public void writeReport(WriteBookReportRequest writeBookReportRequest, Long bookShelfId,
        Long userId) {

        BookShelf bookShelf = bookShelfRepository.findByIdAndUserId(bookShelfId, userId)
            .orElseThrow(BookNotFoundException::new);

        BookReport bookReport = writeBookReportRequest.getBookReport(bookShelf);
        bookShelf.writeReportInStateOfCompleteReading(bookReport);
        bookReportRepository.save(bookReport);
    }

    @Transactional(readOnly = true)
    public BookReportResponse getBookReportResponse(Long bookShelfId, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByIdAndUserId(bookShelfId, userId)
            .orElseThrow(BookNotFoundException::new);

        return BookReportResponse.from(bookShelf.getBookReport());
    }

    @Transactional
    public void deleteBookReport(Long bookShelfId, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByIdAndUserId(bookShelfId, userId)
            .orElseThrow(BookNotFoundException::new);

        bookShelf.deleteBookReport();
    }

    @Transactional
    public void reviseBookReport(Long bookShelfId, Long userId,
        ReviseBookReportRequest reviseBookReportRequest) {
        BookShelf bookShelf = bookShelfRepository.findWithReportByIdAndUserId(bookShelfId,
                userId)
            .orElseThrow(BookNotFoundException::new);
        BookReport bookReport = bookShelf.getBookReport();

        bookReport.reviseTitle(reviseBookReportRequest.getTitle());
        bookReport.reviseContent(reviseBookReportRequest.getContent());
    }
}