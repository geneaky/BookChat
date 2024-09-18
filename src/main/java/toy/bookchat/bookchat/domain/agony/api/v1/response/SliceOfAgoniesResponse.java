package toy.bookchat.bookchat.domain.agony.api.v1.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.support.CursorMeta;

@Getter
public class SliceOfAgoniesResponse {

  private List<AgonyResponse> agonyResponseList;
  private CursorMeta<Agony, Long> cursorMeta;

  public SliceOfAgoniesResponse(Slice<Agony> slice) {
    this.cursorMeta = new CursorMeta<>(slice, Agony::getId);
    this.agonyResponseList = from(slice.getContent());
  }

  private List<AgonyResponse> from(List<Agony> content) {
    return content.stream().map(AgonyResponse::from).collect(Collectors.toList());
  }
}
