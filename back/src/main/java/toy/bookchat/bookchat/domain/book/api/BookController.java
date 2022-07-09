package toy.bookchat.bookchat.domain.book.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class BookController {

    private final BookSearchService bookSearchService;

    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getBookInformation(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @ModelAttribute BookSearchRequestDto bookSearchRequestDto) {

        if (bookSearchRequestDto.isIsbnPresent()) {
            return ResponseEntity.ok(bookSearchService.searchByIsbn(bookSearchRequestDto));
        }

        if (bookSearchRequestDto.isTitlePresent()) {
            return ResponseEntity.ok(bookSearchService.searchByTitle(bookSearchRequestDto));
        }

        if (bookSearchRequestDto.isAuthorPresent()) {
            return ResponseEntity.ok(bookSearchService.searchByAuthor(bookSearchRequestDto));
        }

        return ResponseEntity.badRequest().body(null);
    }
}
