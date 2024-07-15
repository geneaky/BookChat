package toy.bookchat.bookchat.domain.book;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Book {

    private Long id;
    private String title;
    private String isbn;
    private String bookCoverImageUrl;
    private String publisher;
    private LocalDate publishAt;
    private List<String> authors;

    @Builder
    private Book(Long id, String title, String isbn, String bookCoverImageUrl, LocalDate publishAt, List<String> authors, String publisher) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
        this.authors = authors;
        this.publisher = publisher;
    }
}
