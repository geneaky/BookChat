package toy.bookchat.bookchat.domain.book.api;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.domain.book.service.dto.request.BookSearchRequest;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;

@RestController
@RequestMapping("/v1/api")
public class BookController {

    private final BookSearchService bookSearchService;

    public BookController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    @GetMapping("/books")
    public BookSearchResponse getBookInformation(@Valid @ModelAttribute BookSearchRequest bookSearchRequest) {
        return bookSearchService.searchByQuery(bookSearchRequest);
    }
}
