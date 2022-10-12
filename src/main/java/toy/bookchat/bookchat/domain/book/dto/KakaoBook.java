package toy.bookchat.bookchat.domain.book.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoBook {

    private List<Document> documents;
    private Meta meta;

    public BookSearchResponseDto getBookSearchResponseDto() {
        List<BookDto> bookDtos = new ArrayList<>();

        for (Document document : documents) {
            bookDtos.add(
                BookDto.builder()
                    .isbn(document.getIsbn())
                    .title(document.getTitle())
                    .author(document.getAuthors())
                    .publisher(document.getPublisher())
                    .bookCoverImageUrl(document.getThumbnail())
                    .build()
            );
        }

        return BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .meta(meta)
            .build();
    }
}
