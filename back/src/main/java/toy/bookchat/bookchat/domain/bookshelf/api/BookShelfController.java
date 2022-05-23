package toy.bookchat.bookchat.domain.bookshelf.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class BookShelfController {

    private final BookShelfService bookShelfService;

    @PostMapping("/bookshelf/books")
    public ResponseEntity<Void> putBookOnBookShelf(
        @RequestBody BookShelfRequestDto bookShelfRequestDto) {

        bookShelfService.putBookOnBookShelf();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
