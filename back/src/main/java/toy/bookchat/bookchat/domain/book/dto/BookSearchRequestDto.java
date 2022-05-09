package toy.bookchat.bookchat.domain.book.dto;

import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
public class BookSearchRequestDto {

    private String isbn;
    private String title;
    private String author;

    public boolean isIsbnPresent() {
        return StringUtils.hasText(Optional.ofNullable(isbn).orElse(""));
    }

    public boolean isTitlePresent() {
        return StringUtils.hasText(Optional.ofNullable(title).orElse(""));
    }

    public boolean isAuthorPresent() {
        return StringUtils.hasText(Optional.ofNullable(author).orElse(""));
    }
}
