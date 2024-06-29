package toy.bookchat.bookchat.domain.bookshelf.service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@Component
@Transactional(readOnly = true)
public class BookShelfReader {

    private final BookShelfRepository bookShelfRepository;

    public BookShelfReader(BookShelfRepository bookShelfRepository) {
        this.bookShelfRepository = bookShelfRepository;
    }

    public BookShelfEntity readBookShelf(Long bookShelfId, Long userId) {
        return bookShelfRepository.findByIdAndUserId(bookShelfId, userId)
            .orElseThrow(BookNotFoundException::new);
    }


    public Page<BookShelfEntity> readBookShelf(Long userId, ReadingStatus readingStatus, Pageable pageable) {
        return bookShelfRepository.findSpecificStatusBookByUserId(readingStatus, pageable, userId);
    }

    public BookShelfEntity readBookShelf(Long userId, String isbn, LocalDate publishAt) {
        return bookShelfRepository.findByUserIdAndIsbnAndPublishAt(userId, isbn, publishAt)
            .orElseThrow(BookNotFoundException::new);
    }
}
