package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import java.util.List;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookShelfRequestDto {

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
        return new Book(getIsbn(), getTitle(), getAuthors(), getPublisher(),
            getBookCoverImageUrl());
    }

    public void checkCompleteStateField() {
        if (!StringUtils.hasText(this.singleLineAssessment) || this.star == null) {
            throw new IllegalArgumentException();
        }
    }
}
