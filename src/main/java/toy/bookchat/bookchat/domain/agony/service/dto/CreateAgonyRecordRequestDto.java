package toy.bookchat.bookchat.domain.agony.service.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateAgonyRecordRequestDto {

    @NotBlank
    private String title;

    private String content;
    @NotBlank
    private String hexColorCode;

    public AgonyRecord generateAgonyRecord(Agony agony) {
        return new AgonyRecord(this.title, this.content, this.hexColorCode, agony);
    }
}
