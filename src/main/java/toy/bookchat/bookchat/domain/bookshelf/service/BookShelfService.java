package toy.bookchat.bookchat.domain.bookshelf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfResponseDto;
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
        return (book) -> {
            putBookOnBookShelf(bookShelfRequestDto, book, user);
        };
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
    public List<BookShelfResponseDto> takeBooksOutOfBookShelf(ReadingStatus readingStatus,
        Pageable pageable, User user) {

        List<BookShelf> bookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            readingStatus, pageable,
            user.getId());

        return getBookShelfSearchResponseDtos(bookShelves);
    }

    private List<BookShelfResponseDto> getBookShelfSearchResponseDtos(
        List<BookShelf> bookShelves) {
        List<BookShelfResponseDto> bookShelfResponseDtos = new ArrayList<>();

        for (BookShelf bookShelf : bookShelves) {
            BookShelfResponseDto bookShelfResponseDto = BookShelfResponseDto.builder()
                .title(bookShelf.getBookTitle())
                .authors(bookShelf.getBookAuthors())
                .publisher(bookShelf.getBookPublisher())
                .bookCoverImageUrl(bookShelf.getBookCoverImageUrl())
                .star(bookShelf.getStar())
                .singleLineAssessment(bookShelf.getSingleLineAssessment())
                .pages(bookShelf.getPages())
                .build();

            bookShelfResponseDtos.add(bookShelfResponseDto);
        }
        return bookShelfResponseDtos;
    }
}
