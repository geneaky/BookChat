package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@Component
@Transactional(readOnly = true)
public class BookShelfReader {

    private final BookShelfRepository bookShelfRepository;

    public BookShelfReader(BookShelfRepository bookShelfRepository) {
        this.bookShelfRepository = bookShelfRepository;
    }

    public BookShelf readBookShelf(Long bookShelfId, Long userId) {
        return bookShelfRepository.findByIdAndUserId(bookShelfId, userId)
            .orElseThrow(BookNotFoundException::new);
    }
}
