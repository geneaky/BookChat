package toy.bookchat.bookchat.domain.agonyrecord.api.v1.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.support.CursorMeta;

@Getter
public class SliceOfAgonyRecordsResponse {

  private List<AgonyRecordResponse> agonyRecordResponseList;
  private CursorMeta<AgonyRecord, Long> cursorMeta;

  public SliceOfAgonyRecordsResponse(Slice<AgonyRecord> slice) {
    this.cursorMeta = new CursorMeta<>(slice, AgonyRecord::getId);
    this.agonyRecordResponseList = from(slice.getContent());
  }

  private List<AgonyRecordResponse> from(List<AgonyRecord> content) {
    List<AgonyRecordResponse> agonyRecordResponseList = new ArrayList<>();
    for (AgonyRecord agonyRecord : content) {
      agonyRecordResponseList.add(AgonyRecordResponse.from(agonyRecord));
    }

    return agonyRecordResponseList;
  }
}
