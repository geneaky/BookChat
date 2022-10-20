package toy.bookchat.bookchat.domain.bookshelf.api;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import toy.bookchat.bookchat.domain.bookshelf.service.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.ChangeBookStatusRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.ChangeReadingBookPageRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.dto.SearchBookShelfByReadingStatusDto;
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
    public ResponseEntity<SearchBookShelfByReadingStatusDto> takeBookOutOfBookShelf(
        ReadingStatus readingStatus, Pageable pageable,
        @CurrentUser User user
    ) {
        SearchBookShelfByReadingStatusDto searchBookShelfByReadingStatusDto = bookShelfService.takeBooksOutOfBookShelf(
            readingStatus, pageable, user);
        return ResponseEntity.status(HttpStatus.OK).body(searchBookShelfByReadingStatusDto);
    }

    @PatchMapping("/bookshelf/books/{bookId}/pages")
    public ResponseEntity<Void> changeReadingBookPagesOnBookShelf(
        @PathVariable Long bookId,
        @Valid @RequestBody ChangeReadingBookPageRequestDto changeReadingBookPageRequestDto,
        @CurrentUser User user
    ) {
        bookShelfService.changeReadingBookPage(changeReadingBookPageRequestDto, user, bookId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/bookshelf/books/{bookId}/status")
    public ResponseEntity<Void> changeBookStatusOnBookShelf(
        @PathVariable Long bookId,
        @Valid @RequestBody ChangeBookStatusRequestDto changeBookStatusRequestDto,
        @CurrentUser User user) {

        bookShelfService.changeBookStatusOnBookShelf(changeBookStatusRequestDto, user, bookId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/bookshelf/books/{bookId}")
    public ResponseEntity<Void> deleteBookOnBookShelf(
        @PathVariable Long bookId, @CurrentUser User user) {

        bookShelfService.deleteBookOnBookShelf(bookId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
