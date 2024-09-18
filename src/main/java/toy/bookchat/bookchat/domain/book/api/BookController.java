package toy.bookchat.bookchat.domain.book.api;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.domain.book.service.dto.request.BookSearchRequest;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;

@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/api/books")
public class BookController {

  private final BookSearchService bookSearchService;

  @GetMapping
  public BookSearchResponse getBookInformation(@Valid @ModelAttribute BookSearchRequest bookSearchRequest) {
    return bookSearchService.searchByQuery(bookSearchRequest);
  }
}
