package toy.bookchat.bookchat.domain.agony.service.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateAgonyRecordRequest {

    @NotBlank
    private String title;

    private String content;

    public CreateAgonyRecordRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public AgonyRecord generateAgonyRecord(Agony agony) {
        return AgonyRecord.builder()
            .title(this.title)
            .content(this.content)
            .agony(agony)
            .build();
    }
}
