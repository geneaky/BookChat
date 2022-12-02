package toy.bookchat.bookchat.domain.agony.service.dto.response;

import lombok.Getter;

@Getter
public class AgonyRecordResponse {

    private Long agonyRecordId;
    private String agonyRecordTitle;
    private String agonyRecordContent;
    private String createdAt;

    public AgonyRecordResponse(Long agonyRecordId, String agonyRecordTitle,
        String agonyRecordContent, String createdAt) {
        this.agonyRecordId = agonyRecordId;
        this.agonyRecordTitle = agonyRecordTitle;
        this.agonyRecordContent = agonyRecordContent;
        this.createdAt = createdAt;
    }
}
