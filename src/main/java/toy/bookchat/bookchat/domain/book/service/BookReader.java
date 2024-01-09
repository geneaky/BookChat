package toy.bookchat.bookchat.domain.book.service;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;

@Component
public class BookReader {

    private final BookRepository bookRepository;

    public BookReader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book readBook(String isbn, LocalDate publishAt, Book book) {
        return bookRepository.findByIsbnAndPublishAt(isbn, publishAt)
            .orElseGet(() -> bookRepository.save(book));
    }
}
