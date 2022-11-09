package toy.bookchat.bookchat.domain.agony.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.common.PageMeta;

@Getter
public class BasePageOfAgonyRecordsResponse {

    private PageMeta pageMeta;
    private List<AgonyRecordResponse> agonyRecordResponseList;

    public BasePageOfAgonyRecordsResponse(Page<AgonyRecord> page) {
        this.pageMeta = PageMeta.from(page);
        this.agonyRecordResponseList = from(page.getContent());
    }

    private List<AgonyRecordResponse> from(List<AgonyRecord> content) {
        List<AgonyRecordResponse> agonyRecordResponseList = new ArrayList<>();
        for (AgonyRecord agonyRecord : content) {
            agonyRecordResponseList.add(
                new AgonyRecordResponse(agonyRecord.getId(), agonyRecord.getTitle(),
                    agonyRecord.getContent(), agonyRecord.getCreateTimeInYearMonthDayFormat()));
        }

        return agonyRecordResponseList;
    }
}
