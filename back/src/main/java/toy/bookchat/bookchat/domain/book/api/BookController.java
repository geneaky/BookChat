package toy.bookchat.bookchat.domain.book.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.book.dto.BookSearchRequestDto;
import toy.bookchat.bookchat.domain.book.dto.BookSearchResponseDto;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.security.user.UserPrincipal;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class BookController {

    private final BookSearchService bookSearchService;

    @GetMapping("/books")
    public ResponseEntity<BookSearchResponseDto> getBookInformation(@Valid @ModelAttribute BookSearchRequestDto bookSearchRequestDto) {
        return ResponseEntity.ok(bookSearchService.searchByQuery(bookSearchRequestDto));
    }
}
