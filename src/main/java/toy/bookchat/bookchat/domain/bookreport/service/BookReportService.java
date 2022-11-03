package toy.bookchat.bookchat.domain.bookreport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookreport.BookReport;
import toy.bookchat.bookchat.domain.bookreport.repository.BookReportRepository;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.WriteBookReportRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

@Service
public class BookReportService {

    private final BookReportRepository bookReportRepository;
    private final BookShelfRepository bookShelfRepository;

    public BookReportService(BookReportRepository bookReportRepository,
        BookShelfRepository bookShelfRepository) {
        this.bookReportRepository = bookReportRepository;
        this.bookShelfRepository = bookShelfRepository;
    }

    @Transactional
    public void writeReport(WriteBookReportRequestDto writeBookReportRequestDto, Long userId) {

        BookShelf bookShelf = bookShelfRepository.findByUserIdAndBookId(userId,
                writeBookReportRequestDto.getBookShelfId())
            .orElseThrow(() -> {
                throw new BookNotFoundException("Book is not registered on book shelf");
            });

        bookShelf.changeToCompleteReading();
        BookReport bookReport = writeBookReportRequestDto.getBookReport(bookShelf);
        bookReportRepository.save(bookReport);
    }
}
