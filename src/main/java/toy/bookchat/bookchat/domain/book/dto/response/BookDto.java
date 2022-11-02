package toy.bookchat.bookchat.domain.book.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookDto {

    private String isbn;
    private String title;
    private List<String> author;
    private String publisher;
    private String bookCoverImageUrl;

    @Builder
    private BookDto(String isbn, String title, List<String> author, String publisher,
        String bookCoverImageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
    }
}
