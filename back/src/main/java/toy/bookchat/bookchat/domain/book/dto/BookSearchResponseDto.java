package toy.bookchat.bookchat.domain.book.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookSearchResponseDto {

    private List<BookDto> bookDtos;

    private Meta meta;


}
