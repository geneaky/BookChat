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

    public List<BookDto> getBookDtos() {
        List<BookDto> list = new ArrayList<>();

        for (Document document : documents) {
            list.add(
                BookDto.builder()
                    .isbn(document.getIsbn())
                    .title(document.getTitle())
                    .author(document.getAuthors())
                    .publisher(document.getPublisher())
                    .bookCoverImageUrl(document.getThumbnail())
                    .build()
            );
        }

        return list;
    }
}
