package toy.bookchat.bookchat.domain.book.dto;

import java.util.Optional;

import lombok.*;
import org.springframework.util.StringUtils;
import toy.bookchat.bookchat.domain.book.service.BookSearchSort;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookSearchRequestDto {

    @NotBlank
    private String query;

    private Integer size;

    private Integer page;

    private BookSearchSort bookSearchSort;

    @Builder
    protected BookSearchRequestDto(String query, Integer size,
        Integer page,
        BookSearchSort bookSearchSort) {
        this.query = query;
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

}
