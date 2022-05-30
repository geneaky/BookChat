package toy.bookchat.bookchat.domain.bookshelf.dto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.book.model.Book;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookShelfRequestDto {

    @NotBlank
    private String isbn;
    @NotBlank
    private String title;
    @Valid
    @NotNull
    private List<@NotBlank String> author;
    @NotBlank
    private String publisher;
    private String bookCoverImageUrl;
    @NotNull
    private ReadingStatus readingStatus;

    public Book getBook() {
        return Book.builder()
            .isbn(getIsbn())
            .title(getTitle())
            .authors(getAuthor())
            .publisher(getPublisher())
            .bookCoverImageUrl(getBookCoverImageUrl())
            .build();
    }
}
