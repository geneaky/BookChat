package toy.bookchat.bookchat.domain.bookshelf.service.dto.request;

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

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookRequest {

    @NotBlank String isbn;
    @NotBlank String title;
    @Valid
    @NotNull List<String> authors;
    @NotBlank String publisher;
    @NotBlank String bookCoverImageUrl;
    @NotNull LocalDate publishAt;

    @Builder
    private BookRequest(String isbn, String title, List<String> authors, String publisher,
        String bookCoverImageUrl, LocalDate publishAt) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
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
}