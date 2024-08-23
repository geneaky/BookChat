package toy.bookchat.bookchat.domain.bookshelf.service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfWithBook;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Component
@Transactional(readOnly = true)
public class BookShelfReader {

  private final BookShelfRepository bookShelfRepository;

  public BookShelfReader(BookShelfRepository bookShelfRepository) {
    this.bookShelfRepository = bookShelfRepository;
  }

  public BookShelf readBookShelf(Long userId, Long bookShelfId) {
    BookShelfWithBook bookShelfWithBook = bookShelfRepository.findBookShelfWithBook(userId, bookShelfId);

    return bookShelfWithBook.toBookShelf();
  }

  public BookShelf readBookShelf(Long userId, String isbn, LocalDate publishAt) {
    BookShelfWithBook bookShelfWithBook = bookShelfRepository.findByUserIdAndIsbnAndPublishAt(userId, isbn, publishAt);
    return bookShelfWithBook.toBookShelf();
  }

  public Page<BookShelf> readPagedBookShelves(Long userId, ReadingStatus readingStatus, Pageable pageable) {
    Page<BookShelfWithBook> pagedBookShelfWithBook = bookShelfRepository.findBookShelfWithBook(userId, readingStatus,
        pageable);
    return pagedBookShelfWithBook.map(BookShelfWithBook::toBookShelf);
  }
}
