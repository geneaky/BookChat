package toy.bookchat.bookchat.domain.scrap.api.v1.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.scrap.ScrapEntity;

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

  public ScrapEntity create(Long bookShelfId) {
    return ScrapEntity.builder()
        .bookShelfId(bookShelfId)
        .scrapContent(this.scrapContent)
        .build();
  }
}
