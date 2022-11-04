package toy.bookchat.bookchat.domain.book.service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookResponse {

    private String isbn;
    private String title;
    private List<String> author;
    private String publisher;
    private String bookCoverImageUrl;

    @Builder
    private BookResponse(String isbn, String title, List<String> author, String publisher,
        String bookCoverImageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
    }
}