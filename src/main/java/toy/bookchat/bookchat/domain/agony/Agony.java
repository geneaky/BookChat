package toy.bookchat.bookchat.domain.agony;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Agony {

    private Long id;
    private String title;
    private String hexColorCode;

    @Builder
    private Agony(Long id, String title, String hexColorCode) {
        this.id = id;
        this.title = title;
        this.hexColorCode = hexColorCode;
    }

    public void change(AgonyTitleAndColorCode agonyTitleAndColorCode) {
        this.title = agonyTitleAndColorCode.getTitle();
        this.hexColorCode = agonyTitleAndColorCode.getHexColorCode();
    }
}
