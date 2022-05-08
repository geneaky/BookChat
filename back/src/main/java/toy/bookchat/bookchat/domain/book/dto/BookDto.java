package toy.bookchat.bookchat.domain.book.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookDto {

    private String isbn;
    private String title;
    private List<String> author;
    private String bookCoverImageUrl;

}
