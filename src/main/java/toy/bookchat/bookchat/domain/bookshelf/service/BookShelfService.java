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
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeBookStatusRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeReadingBookPageRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatusDto;
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
    public void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto, Long userId) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(bookShelfRequestDto.getIsbn());
        optionalBook.ifPresentOrElse(putBook(bookShelfRequestDto, userId),
            saveBookBeforePuttingBookOnBookShelf(bookShelfRequestDto, userId));
    }

    private Runnable saveBookBeforePuttingBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto,
        Long userId) {
        return () -> {
            Book book = bookRepository.save(bookShelfRequestDto.extractBookEntity());
            putBookOnBookShelf(bookShelfRequestDto, book, userId);
        };
    }

    private Consumer<Book> putBook(BookShelfRequestDto bookShelfRequestDto, Long userId) {
        return book -> putBookOnBookShelf(bookShelfRequestDto, book, userId);
    }

    private void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto, Book book,
        Long userId) {
        BookShelf bookShelf = createBookShelfByReadingStatus(bookShelfRequestDto, book, userId);
        bookShelfRepository.save(bookShelf);
    }

    private BookShelf createBookShelfByReadingStatus(BookShelfRequestDto bookShelfRequestDto,
        Book book, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("Can't find User");
        });
        if (isFinishedReading(bookShelfRequestDto)) {
            bookShelfRequestDto.checkCompleteStateField();

            return BookShelf.builder()
                .book(book)
                .readingStatus(bookShelfRequestDto.getReadingStatus())
                .user(user)
                .star(bookShelfRequestDto.getStar())
                .singleLineAssessment(bookShelfRequestDto.getSingleLineAssessment())
                .build();
        }

        return BookShelf.builder()
            .book(book)
            .readingStatus(bookShelfRequestDto.getReadingStatus())
            .user(user)
            .build();
    }

    private boolean isFinishedReading(BookShelfRequestDto bookShelfRequestDto) {
        return bookShelfRequestDto.getReadingStatus() == ReadingStatus.COMPLETE;
    }

    @Transactional(readOnly = true)
    public SearchBookShelfByReadingStatusDto takeBooksOutOfBookShelf(ReadingStatus readingStatus,
        Pageable pageable, Long userId) {

        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            readingStatus, pageable,
            userId);

        return new SearchBookShelfByReadingStatusDto(pagingBookShelves);
    }

    @Transactional
    public void changeReadingBookPage(
        ChangeReadingBookPageRequestDto changeReadingBookPageRequestDto, Long userId, Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findReadingBookByUserIdAndBookId(userId, bookId);

        bookShelf.updatePage(changeReadingBookPageRequestDto.getPages());
    }

    @Transactional
    public void deleteBookOnBookShelf(Long bookId, Long userId) {

        bookShelfRepository.deleteBookByUserIdAndBookId(userId,
            bookId);
    }

    @Transactional
    public void changeBookStatusOnBookShelf(ChangeBookStatusRequestDto changeBookStatusRequestDto,
        Long userId, Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(
            userId, bookId).orElseThrow(() -> {
            throw new BookNotFoundException("Book is not registered on book shelf");
        });

        bookShelf.updateReadingStatus(changeBookStatusRequestDto.getReadingStatus());
    }
}
