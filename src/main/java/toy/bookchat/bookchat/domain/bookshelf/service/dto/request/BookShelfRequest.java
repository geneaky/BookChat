package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
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
    private String bookCoverImageUrl;
    @NotNull
    private ReadingStatus readingStatus;
    private Star star;
    private String singleLineAssessment;

    public Book extractBookEntity() {
        return Book.builder()
            .isbn(this.isbn)
            .title(this.title)
            .authors(this.authors)
            .publisher(this.publisher)
            .bookCoverImageUrl(this.bookCoverImageUrl)
            .build();
    }

    @Builder
    private BookShelfRequest(String isbn, String title,
        List<@NotBlank String> authors, String publisher, String bookCoverImageUrl,
        ReadingStatus readingStatus, Star star, String singleLineAssessment) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.readingStatus = readingStatus;
        this.star = star;
        this.singleLineAssessment = singleLineAssessment;
    }

    public void checkCompleteStateField() {
        if (!StringUtils.hasText(this.singleLineAssessment) || this.star == null) {
            throw new IllegalArgumentException();
        }
    }
}
