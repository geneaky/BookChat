package toy.bookchat.bookchat.domain.scrap.service.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.scrap.ScrapEntity;

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

    public ScrapEntity create(BookShelfEntity bookShelfEntity) {
        return ScrapEntity.builder()
            .bookShelfEntity(bookShelfEntity)
            .scrapContent(this.scrapContent)
            .build();
    }
}
