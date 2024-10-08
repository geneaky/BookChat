package toy.bookchat.bookchat.domain.scrap.api.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.scrap.ScrapEntity;

@Getter
@EqualsAndHashCode
public class ScrapResponse {

  private Long scrapId;
  private String scrapContent;

  @Builder
  public ScrapResponse(Long scrapId, String scrapContent) {
    this.scrapId = scrapId;
    this.scrapContent = scrapContent;
  }

  public static ScrapResponse from(ScrapEntity scrapEntity) {
    return new ScrapResponse(scrapEntity.getId(), scrapEntity.getScrapContent());
  }
}
