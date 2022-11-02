package toy.bookchat.bookchat.domain.book.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.book.dto.request.Meta;

@Getter
public class BookSearchResponseDto {

    private List<BookDto> bookDtos;
    private Meta meta;

    @Builder
    private BookSearchResponseDto(
        List<BookDto> bookDtos, Meta meta) {
        this.bookDtos = bookDtos;
        this.meta = meta;
    }
}
