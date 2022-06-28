package toy.bookchat.bookchat.domain.bookshelf.service;

import java.util.Optional;
import java.util.function.Consumer;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@Service
@RequiredArgsConstructor
public class BookShelfService {

    private final BookShelfRepository bookShelfRepository;
    private final BookRepository bookRepository;

    @Transactional
    public void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto, Long userId) {

        Optional<Book> optionalBook = bookRepository.findByIsbn(bookShelfRequestDto.getIsbn());

        optionalBook.ifPresentOrElse(putBookOnBookShelf(bookShelfRequestDto),
            saveBookBeforePuttingBookOnBookShelf(bookShelfRequestDto));
    }

    private Runnable saveBookBeforePuttingBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto) {
        return () -> {
            Book book = bookRepository.save(bookShelfRequestDto.getBook());
            putBookOnBookShelf(bookShelfRequestDto);
            BookShelf bookShelf = BookShelf.builder()
                .book(book)
                .readingStatus(bookShelfRequestDto.getReadingStatus())
                .user(null)
                .build();
            bookShelfRepository.save(bookShelf);
        };
    }

    private Consumer<Book> putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto) {
        return (book) -> {
            BookShelf bookShelf = BookShelf.builder()
                .book(book)
                .readingStatus(bookShelfRequestDto.getReadingStatus())
                .user(null)
                .build();

            bookShelfRepository.save(bookShelf);
        };
    }
}
