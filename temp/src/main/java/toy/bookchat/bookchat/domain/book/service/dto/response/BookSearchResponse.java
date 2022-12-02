package toy.bookchat.bookchat.domain.book.service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.book.service.dto.request.Meta;

@Getter
public class BookSearchResponse {

    private List<BookResponse> bookResponses;
    private Meta meta;

    @Builder
    private BookSearchResponse(
        List<BookResponse> bookResponses, Meta meta) {
        this.bookResponses = bookResponses;
        this.meta = meta;
    }
}
