package toy.bookchat.bookchat.domain.scrap.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateScrapRequest {

    @NotBlank
    private String scrapContent;

    @Builder
    private CreateScrapRequest(String scrapContent) {
        this.scrapContent = scrapContent;
    }
}
