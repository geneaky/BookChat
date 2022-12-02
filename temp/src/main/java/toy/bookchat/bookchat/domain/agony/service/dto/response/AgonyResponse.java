package toy.bookchat.bookchat.domain.agony.service.dto.response;

import lombok.Getter;

@Getter
public class AgonyResponse {

    private Long agonyId;
    private String title;
    private String hexColorCode;

    public AgonyResponse(Long agonyId, String title, String hexColorCode) {
        this.agonyId = agonyId;
        this.title = title;
        this.hexColorCode = hexColorCode;
    }
}
