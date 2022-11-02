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
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeBookStatusRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.request.ChangeReadingBookPageRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.response.SearchBookShelfByReadingStatusDto;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.user.CurrentUser;

@RestController
@RequestMapping("/v1/api")
public class BookShelfController {

    private final BookShelfService bookShelfService;

    public BookShelfController(BookShelfService bookShelfService) {
        this.bookShelfService = bookShelfService;
    }

    @PostMapping("/bookshelf/books")
    public void putBookOnBookShelf(@RequestBody @Valid BookShelfRequestDto bookShelfRequestDto,
        @CurrentUser User user) {

        bookShelfService.putBookOnBookShelf(bookShelfRequestDto, user);
    }

    @GetMapping("/bookshelf/books")
    public SearchBookShelfByReadingStatusDto takeBookOutOfBookShelf(ReadingStatus readingStatus,
        Pageable pageable, @CurrentUser User user) {
        return bookShelfService.takeBooksOutOfBookShelf(readingStatus, pageable, user);
    }

    @PatchMapping("/bookshelf/books/{bookId}/pages")
    public void changeReadingBookPagesOnBookShelf(@PathVariable Long bookId,
        @Valid @RequestBody ChangeReadingBookPageRequestDto changeReadingBookPageRequestDto,
        @CurrentUser User user) {

        bookShelfService.changeReadingBookPage(changeReadingBookPageRequestDto, user, bookId);
    }

    @PatchMapping("/bookshelf/books/{bookId}/status")
    public void changeBookStatusOnBookShelf(@PathVariable Long bookId,
        @Valid @RequestBody ChangeBookStatusRequestDto changeBookStatusRequestDto,
        @CurrentUser User user) {

        bookShelfService.changeBookStatusOnBookShelf(changeBookStatusRequestDto, user, bookId);
    }

    @DeleteMapping("/bookshelf/books/{bookId}")
    public void deleteBookOnBookShelf(@PathVariable Long bookId, @CurrentUser User user) {

        bookShelfService.deleteBookOnBookShelf(bookId, user);
    }
}
