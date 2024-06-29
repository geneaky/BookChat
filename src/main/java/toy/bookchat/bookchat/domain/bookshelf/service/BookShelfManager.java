package toy.bookchat.bookchat.domain.bookshelf.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookReportEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@Component
public class BookShelfManager {

    private final BookShelfRepository bookShelfRepository;
    private final BookReportRepository bookReportRepository;
    private final AgonyRepository agonyRepository;
    private final AgonyRecordRepository agonyRecordRepository;


    public BookShelfManager(BookReportRepository bookReportRepository, BookShelfRepository bookShelfRepository, AgonyRepository agonyRepository, AgonyRecordRepository agonyRecordRepository) {
        this.bookReportRepository = bookReportRepository;
        this.bookShelfRepository = bookShelfRepository;
        this.agonyRepository = agonyRepository;
        this.agonyRecordRepository = agonyRecordRepository;
    }

    @Transactional
    public void append(BookShelfEntity bookShelfEntity, BookReportEntity bookReportEntity) {
        bookReportRepository.save(bookReportEntity);
        bookShelfEntity.writeReportInStateOfCompleteReading(bookReportEntity);
    }

    public void store(BookShelfEntity bookShelfEntity) {
        bookShelfRepository.save(bookShelfEntity);
    }

    public void vacate(Long bookShelfId, Long userId) {
        agonyRecordRepository.deleteByBookShelfIdAndUserId(bookShelfId, userId);
        agonyRepository.deleteByBookShelfIdAndUserId(bookShelfId, userId);
        bookShelfRepository.deleteBookShelfByIdAndUserId(bookShelfId, userId);
    }

    public void remove(Long userId) {
        bookShelfRepository.deleteAllByUserId(userId);
    }
}
