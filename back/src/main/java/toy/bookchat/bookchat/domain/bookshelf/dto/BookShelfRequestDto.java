package toy.bookchat.bookchat.domain.bookshelf.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Getter
@Builder
public class BookShelfRequestDto {

    private String isbn;
    private String title;
    private List<String> author;
    private String publisher;
    private String bookCoverImageUrl;
    private ReadingStatus readingStatus;

}
