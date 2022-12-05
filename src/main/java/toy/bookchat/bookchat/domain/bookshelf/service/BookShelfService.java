package toy.bookchat.bookchat.domain.bookshelf.service;

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
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfRequest;
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
        Book book = bookRepository.findByIsbnAndPublishAt(bookShelfRequest.getIsbn(),
                bookShelfRequest.getPublishAt())
            .orElseGet(() -> bookRepository.save(bookShelfRequest.extractBookEntity()));
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        bookShelfRepository.save(createBookShelfByReadingStatus(bookShelfRequest, book, user));
    }

    private BookShelf createBookShelfByReadingStatus(BookShelfRequest bookShelfRequest,
        Book book, User user) {
        if (bookShelfRequest.isCompleteReading()) {
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

    @Transactional(readOnly = true)
    public SearchBookShelfByReadingStatus takeBooksOutOfBookShelf(ReadingStatus readingStatus,
        Pageable pageable, Long userId) {

        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            readingStatus, pageable,
            userId);

        return new SearchBookShelfByReadingStatus(pagingBookShelves);
    }

    /* TODO: 2022-12-05 isbn+publishAt 조건으로 수정해야함
     */
    @Transactional(readOnly = true)
    public ExistenceBookOnBookShelfResponse getBookIfExisted(String isbn, Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByUserIdAndIsbn(userId, isbn)
            .orElseThrow(BookNotFoundException::new);

        return ExistenceBookOnBookShelfResponse.from(bookShelf);
    }

    @Transactional
    public void reviseBookShelf(Long bookId, ReviseBookShelfRequest reviseBookShelfRequest,
        Long userId) {
        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId, bookId)
            .orElseThrow(BookNotFoundException::new);

        reviseBookShelfRequest.applyChanges(bookShelf);
    }

    @Transactional
    public void deleteBookOnBookShelf(Long bookId, Long userId) {

        bookShelfRepository.deleteBookByUserIdAndBookId(userId,
            bookId);
    }

    @Transactional
    public void deleteAllUserBookShelves(Long userId) {
        bookShelfRepository.deleteAllByUserId(userId);
    }

}
