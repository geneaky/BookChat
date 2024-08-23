package toy.bookchat.bookchat.domain.bookshelf.service;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.BookReportTitleAndContent;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Service
public class BookReportService {

  private final BookShelfReader bookShelfReader;
  private final BookShelfManager bookShelfManager;
  private final BookReportAppender bookReportAppender;
  private final BookReportReader bookReportReader;
  private final BookReportManager bookReportManager;


  public BookReportService(BookShelfManager bookShelfManager, BookShelfReader bookShelfReader,
      BookReportAppender bookReportAppender, BookReportReader bookReportReader,
      BookReportManager bookReportManager) {
    this.bookShelfManager = bookShelfManager;
    this.bookShelfReader = bookShelfReader;
    this.bookReportAppender = bookReportAppender;
    this.bookReportReader = bookReportReader;
    this.bookReportManager = bookReportManager;
  }

  @Transactional
  public void writeReport(Long userId, Long bookShelfId, BookReport bookReport) {
    BookShelf bookShelf = bookShelfReader.readBookShelf(userId, bookShelfId);
    bookReportAppender.append(bookShelf, bookReport);
    bookShelfManager.modify(bookShelf, COMPLETE);
  }

  @Transactional(readOnly = true)
  public BookReport getBookReport(Long bookShelfId, Long userId) {
    return bookReportReader.readBookReport(userId, bookShelfId);
  }

  @Transactional
  public void deleteBookReport(Long bookShelfId, Long userId) {
    BookReport bookReport = bookReportReader.readBookReport(userId, bookShelfId);
    bookReportManager.delete(bookReport);
  }

  @Transactional
  public void reviseBookReport(Long bookShelfId, Long userId, BookReportTitleAndContent bookReportTitleAndContent) {
    bookReportManager.modify(userId, bookShelfId, bookReportTitleAndContent);
  }
}
