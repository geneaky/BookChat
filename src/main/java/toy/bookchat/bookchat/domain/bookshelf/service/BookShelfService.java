package toy.bookchat.bookchat.domain.bookshelf.service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfPageAndStarAndReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.service.UserReader;

@Service
@Transactional(readOnly = true)
public class BookShelfService {

  private final BookShelfManager bookShelfManager;
  private final BookShelfReader bookShelfReader;
  private final BookShelfAppender bookShelfAppender;
  private final BookReader bookReader;
  private final UserReader userReader;

  public BookShelfService(UserReader userReader, BookShelfManager bookShelfManager, BookShelfReader bookShelfReader,
      BookShelfAppender bookShelfAppender, BookReader bookReader) {
    this.userReader = userReader;
    this.bookShelfManager = bookShelfManager;
    this.bookShelfReader = bookShelfReader;
    this.bookShelfAppender = bookShelfAppender;
    this.bookReader = bookReader;
  }

  @Transactional(readOnly = true)
  public BookShelf getBookOnBookShelf(Long bookShelfId, Long userId) {
    return bookShelfReader.readBookShelf(userId, bookShelfId);
  }

  @Transactional
  public Long putBookOnBookShelf(BookShelf bookShelf, Book book, Long userId) {
    Book storedBook = bookReader.readBook(book);
    User user = userReader.readUser(userId);
    return bookShelfAppender.append(bookShelf, user, storedBook);
  }

  public Page<BookShelf> takeBooksOutOfBookShelves(ReadingStatus readingStatus, Pageable pageable, Long userId) {
    return bookShelfReader.readPagedBookShelves(userId, readingStatus, pageable);
  }

  public BookShelf getBookIfExisted(String isbn, LocalDate publishAt, Long userId) {
    return bookShelfReader.readBookShelf(userId, isbn, publishAt);
  }

  @Transactional
  public void reviseBookShelf(Long bookShelfId,
      BookShelfPageAndStarAndReadingStatus bookShelfPageAndStarAndReadingStatus, Long userId) {
    BookShelf bookShelf = bookShelfReader.readBookShelf(userId, bookShelfId);

    if (bookShelf.isCompleteReading() && bookShelfPageAndStarAndReadingStatus.hasStar()) {//독서완료 도서 별점 수정
      bookShelf.updateStar(bookShelfPageAndStarAndReadingStatus.getStar());
      bookShelfManager.modify(bookShelf);

      return;
    }

    if (bookShelf.isReading() && bookShelfPageAndStarAndReadingStatus.hasPages()) { //독서중 상태에서만 페이지 쪽수 지정
      bookShelf.updatePage(bookShelfPageAndStarAndReadingStatus.getPages());
      if (bookShelfPageAndStarAndReadingStatus.isReadingComplete()
          && bookShelfPageAndStarAndReadingStatus.hasStar()) { //독서중에서 독서 완료로 변경시 별점이 필수
        bookShelf.updateReadingStatus(bookShelfPageAndStarAndReadingStatus.getReadingStatus());
        bookShelf.updateStar(bookShelfPageAndStarAndReadingStatus.getStar());
      }

      bookShelfManager.modify(bookShelf);

      return;
    }

    if (bookShelf.isWish() && bookShelfPageAndStarAndReadingStatus.isReading()) { // 독서예정 상태에서 독서중으로 변경
      bookShelf.updateReadingStatus(bookShelfPageAndStarAndReadingStatus.getReadingStatus());

      bookShelfManager.modify(bookShelf);
    }
  }

  @Transactional
  public void deleteBookShelf(Long bookShelfId, Long userId) {
    bookShelfManager.vacate(bookShelfId, userId);
  }

  @Transactional
  public void deleteAllUserBookShelves(Long userId) {
    bookShelfManager.remove(userId);
  }

}
