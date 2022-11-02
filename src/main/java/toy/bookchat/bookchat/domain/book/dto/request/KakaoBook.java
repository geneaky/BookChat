package toy.bookchat.bookchat.domain.book.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.book.dto.response.BookDto;
import toy.bookchat.bookchat.domain.book.dto.response.BookSearchResponseDto;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoBook {

    private List<Document> documents;
    private Meta meta;

    public BookSearchResponseDto getBookSearchResponseDto() {
        List<BookDto> bookDtos = new ArrayList<>();
        fillBookDtosWithDocuments(bookDtos);

        return BookSearchResponseDto.builder()
            .bookDtos(bookDtos)
            .meta(meta)
            .build();
    }

    private void fillBookDtosWithDocuments(List<BookDto> bookDtos) {
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
    }
}
