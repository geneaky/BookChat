package toy.bookchat.bookchat.domain.bookshelf.api.v1;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.CreateBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.request.ReviseBookShelfRequest;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.response.BookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.response.ExistenceBookOnBookShelfResponse;
import toy.bookchat.bookchat.domain.bookshelf.api.v1.response.SearchBookShelfByReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPayload;

@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/api/bookshelves")
public class BookShelfController {

  private final BookShelfService bookShelfService;

  @GetMapping("/{bookShelfId}")
  public BookShelfResponse getBookOnBookShelf(@PathVariable Long bookShelfId, @UserPayload TokenPayload tokenPayload) {
    return BookShelfResponse.from(bookShelfService.getBookOnBookShelf(bookShelfId, tokenPayload.getUserId()));
  }


  @PostMapping
  public ResponseEntity<Void> putBookOnBookShelf(@Valid @RequestBody CreateBookShelfRequest createBookShelfRequest,
      @UserPayload TokenPayload tokenPayload) {
    Long bookShelfId = bookShelfService.putBookOnBookShelf(createBookShelfRequest.toTarget(),
        createBookShelfRequest.getBook(), tokenPayload.getUserId());

    return ResponseEntity.status(CREATED)
        .headers(hs -> hs.setLocation(URI.create("/v1/api/bookshelves/" + bookShelfId)))
        .build();
  }

  @GetMapping
  public SearchBookShelfByReadingStatus takeBooksOutOfBookShelves(ReadingStatus readingStatus, Pageable pageable,
      @UserPayload TokenPayload tokenPayload) {
    return new SearchBookShelfByReadingStatus(
        bookShelfService.takeBooksOutOfBookShelves(readingStatus, pageable, tokenPayload.getUserId()));
  }

  @GetMapping("/book")
  public ExistenceBookOnBookShelfResponse findBookIfExistedOnBookShelves(@RequestParam String isbn,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishAt,
      @UserPayload TokenPayload tokenPayload) {
    BookShelf bookShelf = bookShelfService.getBookIfExisted(isbn, publishAt, tokenPayload.getUserId());

    return ExistenceBookOnBookShelfResponse.from(bookShelf);
  }

  @PutMapping("/{bookShelfId}")
  public void reviseBookOnBookShelf(@PathVariable Long bookShelfId,
      @Valid @RequestBody ReviseBookShelfRequest reviseBookShelfStarRequest, @UserPayload TokenPayload tokenPayload) {
    bookShelfService.reviseBookShelf(bookShelfId, reviseBookShelfStarRequest.toTarget(), tokenPayload.getUserId());
  }

  @DeleteMapping("/{bookShelfId}")
  public void deleteBookOnBookShelf(@PathVariable Long bookShelfId, @UserPayload TokenPayload tokenPayload) {
    bookShelfService.deleteBookShelf(bookShelfId, tokenPayload.getUserId());
  }

}
