package toy.bookchat.bookchat.domain.book.api;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.book.dto.request.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.response.BookSearchResponseDto;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;

@RestController
@RequestMapping("/v1/api")
public class BookController {

    private final BookSearchService bookSearchService;

    public BookController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    @GetMapping("/books")
    public BookSearchResponseDto getBookInformation(
        @Valid @ModelAttribute BookSearchRequestDto bookSearchRequestDto) {
        return bookSearchService.searchByQuery(bookSearchRequestDto);
    }
}
