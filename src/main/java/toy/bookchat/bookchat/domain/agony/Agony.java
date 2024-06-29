package toy.bookchat.bookchat.domain.agony;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Agony {

    @Setter
    private Long id;
    private String title;
    private String hexColorCode;

    @Builder
    private Agony(Long id, String title, String hexColorCode) {
        this.id = id;
        this.title = title;
        this.hexColorCode = hexColorCode;
    }

}
