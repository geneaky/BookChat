package toy.bookchat.bookchat.domain.book.service;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.book.Book;

@Component
public class BookReader {

    private final BookRepository bookRepository;

    public BookReader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book readBook(Book book) {
        BookEntity bookEntity = bookRepository.findByIsbnAndPublishAt(book.getIsbn(), book.getPublishAt())
            .orElseGet(storeNewBook(book));

        return Book.builder()
            .id(bookEntity.getId())
            .title(bookEntity.getTitle())
            .isbn(bookEntity.getIsbn())
            .bookCoverImageUrl(bookEntity.getBookCoverImageUrl())
            .publisher(bookEntity.getPublisher())
            .publishAt(bookEntity.getPublishAt())
            .authors(bookEntity.getAuthors())
            .build();
    }

    private Supplier<BookEntity> storeNewBook(Book book) {
        return () -> bookRepository.save(BookEntity.builder()
            .isbn(book.getIsbn())
            .title(book.getTitle())
            .bookCoverImageUrl(book.getBookCoverImageUrl())
            .publisher(book.getPublisher())
            .publishAt(book.getPublishAt())
            .authors(book.getAuthors())
            .build());
    }
}
