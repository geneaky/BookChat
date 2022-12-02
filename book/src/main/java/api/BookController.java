package api;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.BookSearchService;
import service.dto.request.BookSearchRequest;
import service.dto.response.BookSearchResponse;

@RestController
@RequestMapping("/v1/api")
public class BookController {

    private final BookSearchService bookSearchService;

    public BookController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    @GetMapping("/books")
    public BookSearchResponse getBookInformation(
        @Valid @ModelAttribute BookSearchRequest bookSearchRequest) {
        return bookSearchService.searchByQuery(bookSearchRequest);
    }
}
