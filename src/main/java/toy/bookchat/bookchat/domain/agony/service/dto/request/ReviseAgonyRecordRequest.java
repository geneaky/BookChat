package toy.bookchat.bookchat.domain.agony.service.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
