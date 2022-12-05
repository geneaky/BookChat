package toy.bookchat.bookchat.domain.bookshelf.api;

import java.time.LocalDate;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping("/bookshelf/books")
    public void putBookOnBookShelf(@RequestBody @Valid BookShelfRequest bookShelfRequest,
        @UserPayload TokenPayload tokenPayload) {

        bookShelfService.putBookOnBookShelf(bookShelfRequest, tokenPayload.getUserId());
    }

    @GetMapping("/bookshelf/books")
    public SearchBookShelfByReadingStatus takeBookOutOfBookShelf(ReadingStatus readingStatus,
        Pageable pageable, @UserPayload TokenPayload tokenPayload) {
        return bookShelfService.takeBooksOutOfBookShelf(readingStatus, pageable,
            tokenPayload.getUserId());
    }

    @GetMapping("/bookshelf/books/existence")
    public ExistenceBookOnBookShelfResponse findBookIfExistedOnBookShelf(String isbn,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishAt,
        @UserPayload TokenPayload tokenPayload) {

        return bookShelfService.getBookIfExisted(isbn, publishAt, tokenPayload.getUserId());
    }

    @PutMapping("/bookshelf/books/{bookId}")
    public void reviseBookOnBookShelf(@PathVariable Long bookId,
        @Valid @RequestBody ReviseBookShelfRequest reviseBookShelfStarRequest,
        @UserPayload TokenPayload tokenPayload) {

        bookShelfService.reviseBookShelf(bookId, reviseBookShelfStarRequest,
            tokenPayload.getUserId());
    }

    @DeleteMapping("/bookshelf/books/{bookId}")
    public void deleteBookOnBookShelf(@PathVariable Long bookId,
        @UserPayload TokenPayload tokenPayload) {

        bookShelfService.deleteBookOnBookShelf(bookId, tokenPayload.getUserId());
    }

}
