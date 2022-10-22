package toy.bookchat.bookchat.domain.bookreport.api;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookreport.service.BookReportService;
import toy.bookchat.bookchat.domain.bookreport.service.dto.WriteBookReportRequestDto;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.user.CurrentUser;

@RestController
@RequestMapping("/v1/api/bookreports")
public class BookReportController {

    private final BookReportService bookReportService;

    public BookReportController(BookReportService bookReportService) {
        this.bookReportService = bookReportService;
    }

    @PostMapping
    public ResponseEntity<Void> writeBookReport(
        @Valid @RequestBody WriteBookReportRequestDto writeBookReportRequestDto,
        @CurrentUser User user) {

        bookReportService.writeReport(writeBookReportRequestDto, user);
        return ResponseEntity.ok().build();
    }
}
