package toy.bookchat.bookchat.domain.scrap.service.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.scrap.Scrap;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateScrapRequest {

    @NotNull
    private Long bookShelfId;
    @NotBlank
    private String scrapContent;

    @Builder
    private CreateScrapRequest(Long bookShelfId, String scrapContent) {
        this.bookShelfId = bookShelfId;
        this.scrapContent = scrapContent;
    }

    public Scrap create(BookShelf bookShelf) {
        return Scrap.builder()
            .bookShelf(bookShelf)
            .scrapContent(this.scrapContent)
            .build();
    }
}
