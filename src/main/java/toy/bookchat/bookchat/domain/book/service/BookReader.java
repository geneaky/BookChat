package toy.bookchat.bookchat.domain.book.service;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;

@Component
public class BookReader {

    private final BookRepository bookRepository;

    public BookReader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookEntity readBook(String isbn, LocalDate publishAt, BookEntity bookEntity) {
        return bookRepository.findByIsbnAndPublishAt(isbn, publishAt)
            .orElseGet(() -> bookRepository.save(bookEntity));
    }
}
