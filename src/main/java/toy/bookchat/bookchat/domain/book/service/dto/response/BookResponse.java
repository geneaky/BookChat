package toy.bookchat.bookchat.domain.book.service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookResponse {

    private String isbn;
    private String title;
    private String publishAt;
    private List<String> authors;
    private String publisher;
    private String bookCoverImageUrl;

    @Builder
    private BookResponse(String isbn, String title, String publishAt, List<String> authors,
        String publisher,
        String bookCoverImageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.publishAt = publishAt;
        this.authors = authors;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
    }
}
