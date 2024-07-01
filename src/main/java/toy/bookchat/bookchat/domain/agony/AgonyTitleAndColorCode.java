package toy.bookchat.bookchat.domain.agony;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AgonyTitleAndColorCode {

    private String title;
    private String hexColorCode;

    @Builder
    private AgonyTitleAndColorCode(String title, String hexColorCode) {
        this.title = title;
        this.hexColorCode = hexColorCode;
    }
}
