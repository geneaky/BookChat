package toy.bookchat.bookchat.domain.agony.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class SliceOfAgonyRecordsResponse {

    private List<AgonyRecordResponse> agonyRecordResponseList;
    private CursorMeta cursorMeta;

    public SliceOfAgonyRecordsResponse(Slice<AgonyRecord> slice) {
        this.cursorMeta = CursorMeta.from(slice);
        this.agonyRecordResponseList = from(slice.getContent());
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
