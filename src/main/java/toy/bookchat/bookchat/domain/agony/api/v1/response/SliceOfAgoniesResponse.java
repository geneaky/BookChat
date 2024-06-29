package toy.bookchat.bookchat.domain.agony.api.v1.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class SliceOfAgoniesResponse {

    private List<AgonyResponse> agonyResponseList;
    private CursorMeta<AgonyEntity, Long> cursorMeta;

    public SliceOfAgoniesResponse(Slice<AgonyEntity> slice) {
        this.cursorMeta = new CursorMeta<>(slice, AgonyEntity::getId);
        this.agonyResponseList = from(slice.getContent());
    }

    private List<AgonyResponse> from(List<AgonyEntity> content) {
        List<AgonyResponse> result = new ArrayList<>();
        for (AgonyEntity agonyEntity : content) {

            result.add(AgonyResponse.from(agonyEntity));
        }
        return result;
    }
}
