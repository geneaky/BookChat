package toy.bookchat.bookchat.domain.bookshelf.api;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/v1/api")
public class BookShelfController {

    private final BookShelfService bookShelfService;

    public BookShelfController(BookShelfService bookShelfService) {
        this.bookShelfService = bookShelfService;
    }

    @PostMapping("/bookshelves")
    public void putBookOnBookShelf(@RequestBody @Valid BookShelfRequest bookShelfRequest, @UserPayload TokenPayload tokenPayload) {
        bookShelfService.putBookOnBookShelf(bookShelfRequest, tokenPayload.getUserId());
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
