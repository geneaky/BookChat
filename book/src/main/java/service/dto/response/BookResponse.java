package service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookResponse {

    private String isbn;
    private String title;
    private String datetime;
    private List<String> author;
    private String publisher;
    private String bookCoverImageUrl;

    @Builder
    private BookResponse(String isbn, String title, String datetime, List<String> author,
        String publisher,
        String bookCoverImageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.datetime = datetime;
        this.author = author;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
    }
}
