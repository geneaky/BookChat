package toy.bookchat.bookchat.domain.agonyrecord.api.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;

@Getter
@EqualsAndHashCode
public class AgonyRecordResponse {

    private Long agonyRecordId;
    private String agonyRecordTitle;
    private String agonyRecordContent;
    private String createdAt;

    @Builder
    private AgonyRecordResponse(Long agonyRecordId, String agonyRecordTitle, String agonyRecordContent, String createdAt) {
        this.agonyRecordId = agonyRecordId;
        this.agonyRecordTitle = agonyRecordTitle;
        this.agonyRecordContent = agonyRecordContent;
        this.createdAt = createdAt;
    }

    public static AgonyRecordResponse from(AgonyRecord agonyRecord) {
        return new AgonyRecordResponse(agonyRecord.getId(), agonyRecord.getTitle(), agonyRecord.getContent(), agonyRecord.getCreateTimeInYearMonthDayFormat());
    }
}
