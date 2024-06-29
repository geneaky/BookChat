package toy.bookchat.bookchat.domain.bookshelf.service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.book.BookEntity;
import toy.bookchat.bookchat.domain.book.service.BookReader;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.BookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.domain.user.UserEntity;
import toy.bookchat.bookchat.domain.user.service.UserReader;

@Service
@Transactional(readOnly = true)
public class BookShelfService {

    private final BookShelfManager bookShelfManager;
    private final BookShelfReader bookShelfReader;
    private final BookReader bookReader;
    private final UserReader userReader;

    public BookShelfService(UserReader userReader, BookShelfManager bookShelfManager, BookShelfReader bookShelfReader, BookReader bookReader) {
        this.userReader = userReader;
        this.bookShelfManager = bookShelfManager;
        this.bookShelfReader = bookShelfReader;
        this.bookReader = bookReader;
    }

    @Transactional(readOnly = true)
    public BookShelfResponse getBookOnBookShelf(Long bookShelfId, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfReader.readBookShelf(bookShelfId, userId);
        return BookShelfResponse.from(bookShelfEntity);
    }

    @Transactional
    public Long putBookOnBookShelf(BookShelfRequest bookShelfRequest, Long userId) {
        BookEntity bookEntity = bookReader.readBook(bookShelfRequest.getIsbn(), bookShelfRequest.getPublishAt(), bookShelfRequest.extractBookEntity());
        UserEntity userEntity = userReader.readUser(userId);
        BookShelfEntity bookShelfEntity = bookShelfRequest.createBookShelfByReadingStatus(bookEntity, userEntity);
        bookShelfManager.store(bookShelfEntity);

        return bookShelfEntity.getId();
    }

    public SearchBookShelfByReadingStatus takeBooksOutOfBookShelves(ReadingStatus readingStatus, Pageable pageable, Long userId) {
        Page<BookShelfEntity> pagingBookShelves = bookShelfReader.readBookShelf(userId, readingStatus, pageable);
        return new SearchBookShelfByReadingStatus(pagingBookShelves);
    }

    public ExistenceBookOnBookShelfResponse getBookIfExisted(String isbn, LocalDate publishAt, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfReader.readBookShelf(userId, isbn, publishAt);
        return ExistenceBookOnBookShelfResponse.from(bookShelfEntity);
    }

    @Transactional
    public void reviseBookShelf(Long bookShelfId, ReviseBookShelfRequest reviseBookShelfRequest, Long userId) {
        BookShelfEntity bookShelfEntity = bookShelfReader.readBookShelf(bookShelfId, userId);
        reviseBookShelfRequest.applyChanges(bookShelfEntity);
    }

    @Transactional
    public void deleteBookShelf(Long bookShelfId, Long userId) {
        bookShelfManager.vacate(bookShelfId, userId);
    }

    @Transactional
    public void deleteAllUserBookShelves(Long userId) {
        bookShelfManager.remove(userId);
    }

}
