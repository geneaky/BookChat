package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@Component
public class BookShelfManager {

    private final BookShelfRepository bookShelfRepository;
    private final AgonyRepository agonyRepository;
    private final AgonyRecordRepository agonyRecordRepository;


    public BookShelfManager(BookShelfRepository bookShelfRepository, AgonyRepository agonyRepository, AgonyRecordRepository agonyRecordRepository) {
        this.bookShelfRepository = bookShelfRepository;
        this.agonyRepository = agonyRepository;
        this.agonyRecordRepository = agonyRecordRepository;
    }

    public void vacate(Long bookShelfId, Long userId) {
        agonyRecordRepository.deleteByBookShelfIdAndUserId(bookShelfId, userId);
        agonyRepository.deleteByBookShelfIdAndUserId(bookShelfId, userId);
        bookShelfRepository.deleteBookShelfByIdAndUserId(bookShelfId, userId);
    }

    public void remove(Long userId) {
        bookShelfRepository.deleteAllByUserId(userId);
    }

    public void modify(BookShelf bookShelf, ReadingStatus readingStatus) {
        BookShelfEntity bookShelfEntity = bookShelfRepository.findById(bookShelf.getId()).orElseThrow(BookNotFoundException::new);
        bookShelfEntity.updateReadingStatus(readingStatus);
    }

    public void modify(BookShelf bookShelf) {
        BookShelfEntity bookShelfEntity = bookShelfRepository.findById(bookShelf.getId()).orElseThrow(BookNotFoundException::new);
        bookShelfEntity.updateWithoutBook(bookShelf);
    }
}
