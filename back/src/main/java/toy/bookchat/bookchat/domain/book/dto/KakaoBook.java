package toy.bookchat.bookchat.domain.book.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoBook {

    private Meta meta;
    private Document[] documents;

    public List<BookDto> getBookDto() {
        List<BookDto> list = new ArrayList<>();

        for (Document document : documents) {
            list.add(
                BookDto.builder()
                    .isbn(document.getIsbn())
                    .title(document.getTitle())
                    .author(document.getAuthors())
                    .bookCoverImageUrl(document.getThumbnail())
                    .build()
            );
        }

        return list;
    }
}
