package toy.bookchat.bookchat.domain.agonyrecord.api.v1.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class SliceOfAgonyRecordsResponse {

    private List<AgonyRecordResponse> agonyRecordResponseList;
    private CursorMeta<AgonyRecordEntity, Long> cursorMeta;

    public SliceOfAgonyRecordsResponse(Slice<AgonyRecordEntity> slice) {
        this.cursorMeta = new CursorMeta<>(slice, AgonyRecordEntity::getId);
        this.agonyRecordResponseList = from(slice.getContent());
    }

    private List<AgonyRecordResponse> from(List<AgonyRecordEntity> content) {
        List<AgonyRecordResponse> agonyRecordResponseList = new ArrayList<>();
        for (AgonyRecordEntity agonyRecordEntity : content) {
            agonyRecordResponseList.add(AgonyRecordResponse.from(agonyRecordEntity));
        }

        return agonyRecordResponseList;
    }
}
