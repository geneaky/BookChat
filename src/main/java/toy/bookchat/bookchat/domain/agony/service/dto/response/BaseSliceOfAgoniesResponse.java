package toy.bookchat.bookchat.domain.agony.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.common.SliceMeta;

@Getter
public class BaseSliceOfAgoniesResponse {

    private List<AgonyResponse> agonyResponseList;
    private SliceMeta sliceMeta;

    public BaseSliceOfAgoniesResponse(Slice<Agony> slice) {
        this.sliceMeta = SliceMeta.from(slice);
        this.agonyResponseList = from(slice.getContent());
    }

    private List<AgonyResponse> from(List<Agony> content) {
        List<AgonyResponse> result = new ArrayList<>();
        for (Agony agony : content) {
            result.add(new AgonyResponse(agony.getId(), agony.getTitle(), agony.getHexColorCode()));
        }
        return result;
    }
}
