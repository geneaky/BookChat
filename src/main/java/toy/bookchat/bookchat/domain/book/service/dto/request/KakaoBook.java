package toy.bookchat.bookchat.domain.book.service.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookResponse;
import toy.bookchat.bookchat.domain.book.service.dto.response.BookSearchResponse;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class KakaoBook {

    private List<Document> documents;
    private Meta meta;

    public BookSearchResponse getBookSearchResponse() {
        List<BookResponse> bookResponses = new ArrayList<>();
        fillBookResponsesWithDocuments(bookResponses);
        adjustMetaCount(bookResponses);

        return BookSearchResponse.builder()
            .bookResponses(bookResponses)
            .meta(meta)
            .build();
    }

    private void adjustMetaCount(List<BookResponse> bookResponses) {
        meta.changeTotalCount(bookResponses.size());
    }

    private void fillBookResponsesWithDocuments(List<BookResponse> bookResponses) {
        documents.stream().filter(Document::hasPerfectDocument)
            .forEach(document -> bookResponses.add(
                BookResponse.builder()
                    .isbn(document.getIsbn())
                    .title(document.getTitle())
                    .publishAt(document.getYearMonthDay())
                    .authors(document.getAuthors())
                    .publisher(document.getPublisher())
                    .bookCoverImageUrl(document.getThumbnail())
                    .build()
            ));
    }
}
