package toy.bookchat.bookchat.domain.agonyrecord.api.v1.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;

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

    public AgonyRecordEntity generateAgonyRecord(AgonyEntity agonyEntity) {
        return AgonyRecordEntity.builder()
            .title(this.title)
            .content(this.content)
            .agonyId(agonyEntity.getId())
            .build();
    }

    public AgonyRecord toTarget() {
        return AgonyRecord.builder()
            .title(this.title)
            .content(this.content)
            .build();
    }
}
