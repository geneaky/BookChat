package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.book.Book;
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
    private List<@NotBlank String> authors;
    @NotBlank
    private String publisher;
    private String bookCoverImageUrl;
    @NotNull
    private ReadingStatus readingStatus;

    public Book extractBookEntity() {
        return Book.builder()
            .isbn(getIsbn())
            .title(getTitle())
            .authors(getAuthors())
            .publisher(getPublisher())
            .bookCoverImageUrl(getBookCoverImageUrl())
            .build();
    }
}
