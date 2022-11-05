package toy.bookchat.bookchat.domain.bookshelf.api;

import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeBookStatusRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeReadingBookPageRequest;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ReviseBookShelfStarRequest;
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

    @PatchMapping("/bookshelf/books/{bookId}/pages")
    public void changeReadingBookPagesOnBookShelf(@PathVariable Long bookId,
        @Valid @RequestBody ChangeReadingBookPageRequest changeReadingBookPageRequest,
        @UserPayload TokenPayload tokenPayload) {

        bookShelfService.changeReadingBookPage(changeReadingBookPageRequest,
            tokenPayload.getUserId(), bookId);
    }

    @PatchMapping("/bookshelf/books/{bookId}/status")
    public void changeBookStatusOnBookShelf(@PathVariable Long bookId,
        @Valid @RequestBody ChangeBookStatusRequest changeBookStatusRequest,
        @UserPayload TokenPayload tokenPayload) {

        bookShelfService.changeBookStatusOnBookShelf(changeBookStatusRequest,
            tokenPayload.getUserId(), bookId);
    }

    @DeleteMapping("/bookshelf/books/{bookId}")
    public void deleteBookOnBookShelf(@PathVariable Long bookId,
        @UserPayload TokenPayload tokenPayload) {

        bookShelfService.deleteBookOnBookShelf(bookId, tokenPayload.getUserId());
    }

    @PatchMapping("/bookshelf/books/{bookId}/star")
    public void changeBookStarOnBookShelf(@PathVariable Long bookId, @Valid @RequestBody
    ReviseBookShelfStarRequest reviseBookShelfStarRequest, @UserPayload TokenPayload tokenPayload) {

        bookShelfService.reviseBookStar(bookId, tokenPayload.getUserId(),
            reviseBookShelfStarRequest);
    }
}
