package toy.bookchat.bookchat.domain.bookshelf.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfSearchResponseDto;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookShelfService {

    private final BookShelfRepository bookShelfRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto, User user) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(bookShelfRequestDto.getIsbn());
        optionalBook.ifPresentOrElse(putBook(bookShelfRequestDto, user),
            saveBookBeforePuttingBookOnBookShelf(bookShelfRequestDto, user));
    }

    private Runnable saveBookBeforePuttingBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto,
        User user) {
        return () -> {
            Book book = bookRepository.save(bookShelfRequestDto.extractBookEntity());
            putBookOnBookShelf(bookShelfRequestDto, book, user);
        };
    }

    private Consumer<Book> putBook(BookShelfRequestDto bookShelfRequestDto, User user) {
        return (book) -> {
            putBookOnBookShelf(bookShelfRequestDto, book, user);
        };
    }

    private void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto, Book book, User user) {
        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .readingStatus(bookShelfRequestDto.getReadingStatus())
            .user(user)
            .build();

        bookShelfRepository.save(bookShelf);
    }

    public List<BookShelfSearchResponseDto> takeBookOutOfBookShelf(ReadingStatus readingStatus,
        Pageable pageable, User user) {

//        bookShelfRepository.findSpecificStateBookByUserId(readingStatus, pageable, user.getId());
        return null;
    }
}
