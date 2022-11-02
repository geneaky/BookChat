package toy.bookchat.bookchat.domain.book.dto.request;

import java.util.Optional;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import toy.bookchat.bookchat.domain.book.service.BookSearchSort;

@Getter
@Setter
public class BookSearchRequestDto {

    @NotBlank
    private String query;
    private Integer size;
    private Integer page;
    private BookSearchSort sort;

    @Builder
    private BookSearchRequestDto(String query, Integer size, Integer page,
        BookSearchSort sort) {
        this.query = query;
        this.size = size;
        this.page = page;
        this.sort = sort;
    }

    public Optional<Integer> getPage() {
        return Optional.ofNullable(this.page);
    }

    public Optional<Integer> getSize() {
        return Optional.ofNullable(this.size);
    }

    public Optional<BookSearchSort> getSort() {
        return Optional.ofNullable(this.sort);
    }

}
