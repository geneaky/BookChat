package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.WriteBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.BookReportResponse;

@Service
public class BookReportService {

    private final BookShelfManager bookShelfManager;
    private final BookShelfReader bookShelfReader;

    public BookReportService(BookShelfManager bookShelfManager, BookShelfReader bookShelfReader) {
        this.bookShelfManager = bookShelfManager;
        this.bookShelfReader = bookShelfReader;
    }

    @Transactional
    public void writeReport(WriteBookReportRequest writeBookReportRequest, Long bookShelfId,
        Long userId) {
        BookShelf bookShelf = bookShelfReader.readBookShelf(bookShelfId, userId);
        BookReport bookReport = writeBookReportRequest.getBookReport(bookShelf);
        bookShelfManager.append(bookShelf, bookReport);
    }

    @Transactional(readOnly = true)
    public BookReportResponse getBookReportResponse(Long bookShelfId, Long userId) {
        BookShelf bookShelf = bookShelfReader.readBookShelf(bookShelfId, userId);
        return BookReportResponse.from(bookShelf.getBookReport());
    }

    @Transactional
    public void deleteBookReport(Long bookShelfId, Long userId) {
        BookShelf bookShelf = bookShelfReader.readBookShelf(bookShelfId, userId);
        bookShelf.deleteBookReport();
    }

    @Transactional
    public void reviseBookReport(Long bookShelfId, Long userId,
        ReviseBookReportRequest reviseBookReportRequest) {
        BookShelf bookShelf = bookShelfReader.readBookShelf(bookShelfId, userId);
        reviseBookReportRequest.revise(bookShelf.getBookReport());
    }
}
