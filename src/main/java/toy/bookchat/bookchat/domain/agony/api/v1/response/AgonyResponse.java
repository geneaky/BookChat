package toy.bookchat.bookchat.domain.agony.api.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;

@Getter
@EqualsAndHashCode
public class AgonyResponse {

    private Long agonyId;
    private String title;
    private String hexColorCode;

    @Builder
    private AgonyResponse(Long agonyId, String title, String hexColorCode) {
        this.agonyId = agonyId;
        this.title = title;
        this.hexColorCode = hexColorCode;
    }

    public static AgonyResponse from(AgonyEntity agonyEntity) {
        return new AgonyResponse(agonyEntity.getId(), agonyEntity.getTitle(), agonyEntity.getHexColorCode());
    }
}
