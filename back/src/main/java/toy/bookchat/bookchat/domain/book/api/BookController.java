package toy.bookchat.bookchat.domain.book.api;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class BookController {

    private BookSearchService bookSearchService;

    @GetMapping("/books")
    public ResponseEntity<BookDto> getBookInformation(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @RequestParam(name = "isbn", required = false) Optional<String> isbn,
        @RequestParam(name = "bookName", required = false) Optional<String> bookName,
        @RequestParam(name = "author", required = false) Optional<String> author) {

        return new ResponseEntity<BookDto>(bookSearchService.search(isbn.get()), HttpStatus.OK);
    }
}
