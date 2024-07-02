package toy.bookchat.bookchat.domain.agonyrecord.api.v1.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecordTitleAndContent;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviseAgonyRecordRequest {

    private String recordTitle;
    private String recordContent;

    @Builder
    private ReviseAgonyRecordRequest(String recordTitle, String recordContent) {
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
    }

    public AgonyRecordTitleAndContent toTarget() {
        return AgonyRecordTitleAndContent.builder()
            .title(this.recordTitle)
            .content(this.recordContent)
            .build();
    }
}
