package toy.bookchat.bookchat.domain.bookshelf.api;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.user.CurrentUser;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class BookShelfController {

    private final BookShelfService bookShelfService;

    @PostMapping("/bookshelf/books")
    public ResponseEntity<Void> putBookOnBookShelf(
        @RequestBody @Valid BookShelfRequestDto bookShelfRequestDto,
        @CurrentUser User user) {

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
