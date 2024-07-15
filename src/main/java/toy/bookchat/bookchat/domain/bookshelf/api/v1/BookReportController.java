package toy.bookchat.bookchat.domain.bookshelf.api.v1;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookshelf.BookReport;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.ReviseBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.WriteBookReportRequest;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.response.BookReportResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.BookReportService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api/bookshelves/{bookShelfId}/report")
public class BookReportController {

    private final BookReportService bookReportService;

    public BookReportController(BookReportService bookReportService) {
        this.bookReportService = bookReportService;
    }

    @PostMapping
    public void writeBookReport(@Valid @RequestBody WriteBookReportRequest writeBookReportRequest, @PathVariable Long bookShelfId, @UserPayload TokenPayload tokenPayload) {
        bookReportService.writeReport(tokenPayload.getUserId(), bookShelfId, writeBookReportRequest.toTarget());
    }

    @GetMapping
    public BookReportResponse findBookReportFromBook(@PathVariable Long bookShelfId, @UserPayload TokenPayload tokenPayload) {
        BookReport bookReport = bookReportService.getBookReport(bookShelfId, tokenPayload.getUserId());

        return BookReportResponse.from(bookReport);
    }

    @DeleteMapping
    public void deleteBookReport(@PathVariable Long bookShelfId, @UserPayload TokenPayload tokenPayload) {
        bookReportService.deleteBookReport(bookShelfId, tokenPayload.getUserId());
    }

    @PutMapping
    public void reviseBookReport(@PathVariable Long bookShelfId, @Valid @RequestBody ReviseBookReportRequest reviseBookReportRequest, @UserPayload TokenPayload tokenPayload) {
        bookReportService.reviseBookReport(bookShelfId, tokenPayload.getUserId(), reviseBookReportRequest.toTarget());
    }
}
