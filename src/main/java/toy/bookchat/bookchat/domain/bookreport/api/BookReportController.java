package toy.bookchat.bookchat.domain.bookreport.api;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookreport.service.BookReportService;
import toy.bookchat.bookchat.domain.bookreport.service.dto.request.WriteBookReportRequestDto;
import toy.bookchat.bookchat.domain.bookreport.service.dto.response.BookReportResponse;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api/books/{bookId}/report")
public class BookReportController {

    private final BookReportService bookReportService;

    public BookReportController(BookReportService bookReportService) {
        this.bookReportService = bookReportService;
    }

    @PostMapping
    public void writeBookReport(
        @Valid @RequestBody WriteBookReportRequestDto writeBookReportRequestDto,
        @PathVariable Long bookId, @UserPayload TokenPayload tokenPayload) {

        bookReportService.writeReport(writeBookReportRequestDto, bookId, tokenPayload.getUserId());
    }

    @GetMapping
    public BookReportResponse findBookReportFromBook(@PathVariable Long bookId,
        @UserPayload TokenPayload tokenPayload) {

        return bookReportService.getBookReportResponse(bookId, tokenPayload.getUserId());
    }
}
