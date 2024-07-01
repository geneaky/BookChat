package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.bookreport.BookReportEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
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
    public void writeReport(WriteBookReportRequest writeBookReportRequest, Long bookShelfId, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfReader.readBookShelfEntity(bookShelfId, userId);
        BookReportEntity bookReportEntity = writeBookReportRequest.getBookReport(bookShelfEntity);
        bookShelfManager.append(bookShelfEntity, bookReportEntity);
    }

    @Transactional(readOnly = true)
    public BookReportResponse getBookReportResponse(Long bookShelfId, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfReader.readBookShelfEntity(bookShelfId, userId);
        return BookReportResponse.from(bookShelfEntity.getBookReportEntity());
    }

    @Transactional
    public void deleteBookReport(Long bookShelfId, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfReader.readBookShelfEntity(bookShelfId, userId);
        bookShelfEntity.deleteBookReport();
    }

    @Transactional
    public void reviseBookReport(Long bookShelfId, Long userId, ReviseBookReportRequest reviseBookReportRequest) {
        BookShelfEntity bookShelfEntity = bookShelfReader.readBookShelfEntity(bookShelfId, userId);
        reviseBookReportRequest.revise(bookShelfEntity.getBookReportEntity());
    }
}
