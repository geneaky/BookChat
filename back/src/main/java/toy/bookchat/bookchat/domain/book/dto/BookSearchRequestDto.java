package toy.bookchat.bookchat.domain.book.dto;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.domain.book.service.BookSearchSort;

@Getter
@Setter
@NoArgsConstructor
public class BookSearchRequestDto {

    private String isbn;
    private String title;
    private String author;

    private Integer size;

    private Integer page;

    private BookSearchSort bookSearchSort;

    @Builder
    protected BookSearchRequestDto(String isbn, String title, String author, Integer size,
        Integer page,
        BookSearchSort bookSearchSort) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.size = size;
        this.page = page;
        this.bookSearchSort = bookSearchSort;
    }

    public Optional<Integer> getPage() {
        return Optional.ofNullable(this.page);
    }

    public Optional<Integer> getSize() {
        return Optional.ofNullable(this.size);
    }

    public Optional<BookSearchSort> getBookSearchSort() {
        return Optional.ofNullable(this.bookSearchSort);
    }

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
