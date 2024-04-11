package toy.bookchat.bookchat.domain.bookshelf.api;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import java.time.LocalDate;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.BookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RestController
@RequestMapping("/v1/api")
public class BookShelfController {

    private final BookShelfService bookShelfService;

    public BookShelfController(BookShelfService bookShelfService) {
        this.bookShelfService = bookShelfService;
    }

    @GetMapping("/bookshelves/{bookShelfId}")
    public BookShelfResponse getBookOnBookShelf(@PathVariable Long bookShelfId, @UserPayload TokenPayload tokenPayload) {
        return bookShelfService.getBookOnBookShelf(bookShelfId, tokenPayload.getUserId());
    }


    @PostMapping("/bookshelves")
    public ResponseEntity<Void> putBookOnBookShelf(@Valid @RequestBody BookShelfRequest bookShelfRequest, @UserPayload TokenPayload tokenPayload) {
        Long bookShelfId = bookShelfService.putBookOnBookShelf(bookShelfRequest, tokenPayload.getUserId());

        return ResponseEntity.status(CREATED)
            .headers(hs -> hs.setLocation(URI.create("/v1/api/bookshelves/" + bookShelfId)))
            .build();
    }

    @GetMapping("/bookshelves")
    public SearchBookShelfByReadingStatus takeBooksOutOfBookShelves(ReadingStatus readingStatus, Pageable pageable, @UserPayload TokenPayload tokenPayload) {
        return bookShelfService.takeBooksOutOfBookShelves(readingStatus, pageable, tokenPayload.getUserId());
    }

    @GetMapping("/bookshelves/book")
    public ExistenceBookOnBookShelfResponse findBookIfExistedOnBookShelves(String isbn, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishAt, @UserPayload TokenPayload tokenPayload) {
        return bookShelfService.getBookIfExisted(isbn, publishAt, tokenPayload.getUserId());
    }

    @PutMapping("/bookshelves/{bookShelfId}")
    public void reviseBookOnBookShelf(@PathVariable Long bookShelfId, @Valid @RequestBody ReviseBookShelfRequest reviseBookShelfStarRequest, @UserPayload TokenPayload tokenPayload) {
        bookShelfService.reviseBookShelf(bookShelfId, reviseBookShelfStarRequest, tokenPayload.getUserId());
    }

    @DeleteMapping("/bookshelves/{bookShelfId}")
    public void deleteBookOnBookShelf(@PathVariable Long bookShelfId, @UserPayload TokenPayload tokenPayload) {
        bookShelfService.deleteBookShelf(bookShelfId, tokenPayload.getUserId());
    }

}
