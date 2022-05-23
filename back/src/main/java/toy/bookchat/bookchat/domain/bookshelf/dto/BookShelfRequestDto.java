package toy.bookchat.bookchat.domain.bookshelf.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookShelfRequestDto {

    @NotBlank
    private String isbn;
    @NotBlank
    private String title;
    @NotNull
    private List<String> author;
    @NotBlank
    private String publisher;
    @NotBlank
    private String bookCoverImageUrl;
    @NotNull
    private ReadingStatus readingStatus;

}
