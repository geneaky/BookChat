package toy.bookchat.bookchat.domain.bookshelf.api;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfResponseDto;
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

    @GetMapping("/bookshelf/books")
    public ResponseEntity<List<BookShelfResponseDto>> takeBookOutOfBookShelf(
        ReadingStatus readingStatus, Pageable pageable,
        @CurrentUser User user
    ) {
        List<BookShelfResponseDto> bookShelfResponseDtos = bookShelfService.takeBooksOutOfBookShelf(
            readingStatus, pageable, user);
        return ResponseEntity.status(HttpStatus.OK).body(bookShelfResponseDtos);
    }
}
