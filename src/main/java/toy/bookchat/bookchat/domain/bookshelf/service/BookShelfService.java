package toy.bookchat.bookchat.domain.bookshelf.service;

import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeBookStatusRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeReadingBookPageRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfStarRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class BookShelfService {

    private final BookShelfRepository bookShelfRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookShelfService(BookShelfRepository bookShelfRepository,
        BookRepository bookRepository,
        UserRepository userRepository) {
        this.bookShelfRepository = bookShelfRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void putBookOnBookShelf(BookShelfRequest bookShelfRequest, Long userId) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(bookShelfRequest.getIsbn());
        optionalBook.ifPresentOrElse(putBook(bookShelfRequest, userId),
            saveBookBeforePuttingBookOnBookShelf(bookShelfRequest, userId));
    }

    private Runnable saveBookBeforePuttingBookOnBookShelf(BookShelfRequest bookShelfRequest,
        Long userId) {
        return () -> {
            Book book = bookRepository.save(bookShelfRequest.extractBookEntity());
            putBookOnBookShelf(bookShelfRequest, book, userId);
        };
    }

    private Consumer<Book> putBook(BookShelfRequest bookShelfRequest, Long userId) {
        return book -> putBookOnBookShelf(bookShelfRequest, book, userId);
    }

    private void putBookOnBookShelf(BookShelfRequest bookShelfRequest, Book book,
        Long userId) {
        BookShelf bookShelf = createBookShelfByReadingStatus(bookShelfRequest, book, userId);
        bookShelfRepository.save(bookShelf);
    }

    private BookShelf createBookShelfByReadingStatus(BookShelfRequest bookShelfRequest,
        Book book, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (isFinishedReading(bookShelfRequest)) {
            bookShelfRequest.checkCompleteStateField();

            return BookShelf.builder()
                .book(book)
                .readingStatus(bookShelfRequest.getReadingStatus())
                .user(user)
                .star(bookShelfRequest.getStar())
                .build();
        }

        return BookShelf.builder()
            .book(book)
            .readingStatus(bookShelfRequest.getReadingStatus())
            .user(user)
            .build();
    }

    private boolean isFinishedReading(BookShelfRequest bookShelfRequest) {
        return bookShelfRequest.getReadingStatus() == ReadingStatus.COMPLETE;
    }

    @Transactional(readOnly = true)
    public SearchBookShelfByReadingStatus takeBooksOutOfBookShelf(ReadingStatus readingStatus,
        Pageable pageable, Long userId) {

        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            readingStatus, pageable,
            userId);

        return new SearchBookShelfByReadingStatus(pagingBookShelves);
    }

    @Transactional
    public void changeReadingBookPage(
        ChangeReadingBookPageRequest changeReadingBookPageRequest, Long userId, Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findOneOnConditionByUserIdAndBookId(userId,
            bookId, ReadingStatus.READING);

        bookShelf.updatePage(changeReadingBookPageRequest.getPages());
    }

    @Transactional
    public void deleteBookOnBookShelf(Long bookId, Long userId) {

        bookShelfRepository.deleteBookByUserIdAndBookId(userId,
            bookId);
    }

    @Transactional
    public void changeBookStatusOnBookShelf(ChangeBookStatusRequest changeBookStatusRequest,
        Long userId, Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(
            userId, bookId).orElseThrow(BookNotFoundException::new);

        bookShelf.updateReadingStatus(changeBookStatusRequest.getReadingStatus());
    }

    @Transactional
    public void reviseBookStar(Long bookId, Long userId,
        ReviseBookShelfStarRequest reviseBookShelfStarRequest) {
        BookShelf bookShelf = bookShelfRepository.findOneOnConditionByUserIdAndBookId(
            userId, bookId, ReadingStatus.COMPLETE);

        bookShelf.changeStar(reviseBookShelfStarRequest.getStar());
    }

    @Transactional
    public void deleteAllUserBookShelves(Long userId) {
        bookShelfRepository.deleteAllByUserId(userId);
    }

    public ExistenceBookOnBookShelfResponse getBookIfExisted(String isbn, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByUserIdAndIsbn(userId, isbn)
            .orElseThrow(BookNotFoundException::new);

        return ExistenceBookOnBookShelfResponse.from(bookShelf);
    }
}
