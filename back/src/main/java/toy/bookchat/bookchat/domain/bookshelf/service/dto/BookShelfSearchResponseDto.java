package toy.bookchat.bookchat.domain.bookshelf.service.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookShelfSearchResponseDto {

    private String title;
    private String bookCoverImageUrl;
    private List<String> authors;
    private String publisher;
    private Float star;
    private String singleLineAssessment;
}
