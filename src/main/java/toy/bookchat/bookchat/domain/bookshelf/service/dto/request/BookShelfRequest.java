package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookShelfRequest {

    @NotBlank
    private String isbn;
    @NotBlank
    private String title;
    @Valid
    @NotNull
    private List<@NotBlank String> authors;
    @NotBlank
    private String publisher;
    @NotBlank
    private String bookCoverImageUrl;
    @NotNull
    private LocalDate publishAt;
    @NotNull
    private ReadingStatus readingStatus;
    private Star star;

    @Builder
    private BookShelfRequest(String isbn, String title,
        List<@NotBlank String> authors, String publisher, String bookCoverImageUrl,
        ReadingStatus readingStatus, Star star, LocalDate publishAt) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
        this.readingStatus = readingStatus;
        this.star = star;
    }

    public Book extractBookEntity() {
        return Book.builder()
            .isbn(this.isbn)
            .title(this.title)
            .authors(this.authors)
            .publisher(this.publisher)
            .bookCoverImageUrl(this.bookCoverImageUrl)
            .publishAt(this.publishAt)
            .build();
    }

    public boolean isFinishedReadingiriri() {
        if (this.readingStatus == COMPLETE) {
            return isEvaluated();
        }
        return false;
    }

    private boolean isEvaluated() {
        if (this.star == null) {
            throw new IllegalStateException(
                "Star is required to change bookshelf complete reading status");
        }
        return true;
    }
}
