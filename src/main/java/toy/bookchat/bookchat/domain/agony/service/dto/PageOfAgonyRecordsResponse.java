package toy.bookchat.bookchat.domain.agony.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.common.PageDto;

@Getter
public class PageOfAgonyRecordsResponse extends PageDto {

    private final List<AgonyRecordResponse> agonyRecordResponseList;

    public PageOfAgonyRecordsResponse(Page<AgonyRecord> page) {
        super(page);
        agonyRecordResponseList = from(page.getContent());
    }

    private List<AgonyRecordResponse> from(List<AgonyRecord> content) {
        List<AgonyRecordResponse> agonyRecordResponseList = new ArrayList<>();
        for (AgonyRecord agonyRecord : content) {
            agonyRecordResponseList.add(
                new AgonyRecordResponse(agonyRecord.getId(), agonyRecord.getTitle(),
                    agonyRecord.getContent(), agonyRecord.getCreatedAt()));
        }

        return agonyRecordResponseList;
    }
}
