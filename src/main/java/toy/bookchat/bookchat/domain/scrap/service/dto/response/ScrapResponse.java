package toy.bookchat.bookchat.domain.scrap.service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ScrapResponse {

    private Long scrapId;
    private String scrapContent;

    @Builder
    public ScrapResponse(Long scrapId, String scrapContent) {
        this.scrapId = scrapId;
        this.scrapContent = scrapContent;
    }
}
