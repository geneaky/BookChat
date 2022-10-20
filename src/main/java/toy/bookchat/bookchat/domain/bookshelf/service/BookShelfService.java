package toy.bookchat.bookchat.domain.bookshelf.service;

import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.ChangeBookStatusRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.ChangeReadingBookPageRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.SearchBookShelfByReadingStatusDto;
import toy.bookchat.bookchat.domain.user.User;

@Service
@RequiredArgsConstructor
public class BookShelfService {

    private final BookShelfRepository bookShelfRepository;
    private final BookRepository bookRepository;

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
        return book -> putBookOnBookShelf(bookShelfRequestDto, book, user);
    }

    private void putBookOnBookShelf(BookShelfRequestDto bookShelfRequestDto, Book book, User user) {
        BookShelf bookShelf = createBookShelfByReadingStatus(bookShelfRequestDto, book, user);
        bookShelfRepository.save(bookShelf);
    }

    private BookShelf createBookShelfByReadingStatus(BookShelfRequestDto bookShelfRequestDto,
        Book book, User user) {
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
        Pageable pageable, User user) {

        Page<BookShelf> pagingBookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            readingStatus, pageable,
            user.getId());

        return new SearchBookShelfByReadingStatusDto(pagingBookShelves);
    }

    @Transactional
    public void changeReadingBookPage(
        ChangeReadingBookPageRequestDto changeReadingBookPageRequestDto, User user, Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findReadingBookByUserIdAndBookId(user.getId(),
            bookId);

        bookShelf.updatePage(changeReadingBookPageRequestDto.getPages());
    }

    @Transactional
    public void deleteBookOnBookShelf(Long bookId, User user) {

        bookShelfRepository.deleteBookByUserIdAndBookId(user.getId(),
            bookId);
    }

    @Transactional
    public void changeBookStatusOnBookShelf(ChangeBookStatusRequestDto changeBookStatusRequestDto,
        User user, Long bookId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(
            user.getId(), bookId);

        bookShelf.updateReadingStatus(changeBookStatusRequestDto.getReadingStatus());
    }
}
