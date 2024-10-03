package toy.bookchat.bookchat.domain.bookshelf.api.v1.request;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBookShelfRequest {

  @Valid
  @NotNull
  private BookRequest bookRequest;
  @NotNull
  private ReadingStatus readingStatus;
  private Star star;

  @Builder
  private CreateBookShelfRequest(BookRequest bookRequest, ReadingStatus readingStatus, Star star) {
    this.bookRequest = bookRequest;
    this.readingStatus = readingStatus;
    this.star = star;
  }

  @JsonIgnore
  public Book getBook() {
    return this.bookRequest.extractBook();
  }

  @JsonIgnore
  public boolean isCompleteReading() {
    if (this.readingStatus == COMPLETE) {
      return isEvaluated();
    }
    return false;
  }

  private boolean isEvaluated() {
    if (this.star == null) {
      throw new IllegalStateException("Star is required to change bookshelf complete reading status");
    }
    return true;
  }

  @JsonIgnore
  public String getIsbn() {
    return this.bookRequest.getIsbn();
  }

  @JsonIgnore
  public LocalDate getPublishAt() {
    return this.bookRequest.getPublishAt();
  }

  public BookShelf toTarget() {
    if (this.isCompleteReading()) {
      return BookShelf.builder()
          .readingStatus(this.readingStatus)
          .star(this.star)
          .pages(0)
          .build();
    }
    return BookShelf.builder()
        .readingStatus(this.readingStatus)
        .pages(0)
        .build();
  }
}
