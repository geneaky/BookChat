package toy.bookchat.bookchat.domain.book.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookDto {

    private String isbn;
    private String title;
    private String author;
    private String bookCoverImageUrl;

}
