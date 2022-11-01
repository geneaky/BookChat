package toy.bookchat.bookchat.domain.agony.service.dto;

import lombok.Getter;

@Getter
public class AgonyRecordResponse {

    private final Long agonyRecordId;
    private final String agonyRecordTitle;
    private final String agonyRecordContent;
    private final String createdAt;

    public AgonyRecordResponse(Long agonyRecordId, String agonyRecordTitle,
        String agonyRecordContent,
        String createdAt) {
        this.agonyRecordId = agonyRecordId;
        this.agonyRecordTitle = agonyRecordTitle;
        this.agonyRecordContent = agonyRecordContent;
        this.createdAt = createdAt;
    }
}
