package toy.bookchat.bookchat.domain.bookshelf.api.v1.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.data.domain.Page;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.support.PageMeta;

@Getter
public class SearchBookShelfByReadingStatus {

  private PageMeta pageMeta;
  private List<BookShelfResponse> contents;

  public SearchBookShelfByReadingStatus(Page<BookShelf> pagedBookShelf) {
    this.pageMeta = PageMeta.from(pagedBookShelf);
    this.contents = getBookShelfSearchResponseDtos(pagedBookShelf.getContent());
  }

  private List<BookShelfResponse> getBookShelfSearchResponseDtos(List<BookShelf> bookShelves) {
    return bookShelves.stream()
        .map(BookShelfResponse::from)
        .collect(Collectors.toList());
  }
}
