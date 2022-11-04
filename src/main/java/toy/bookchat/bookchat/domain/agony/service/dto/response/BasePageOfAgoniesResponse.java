package toy.bookchat.bookchat.domain.agony.service.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.common.BasePage;

@Getter
public class BasePageOfAgoniesResponse extends BasePage {

    private List<AgonyResponse> agonyResponseList;

    public BasePageOfAgoniesResponse(Page<Agony> page) {
        super(page);
        this.agonyResponseList = from(page.getContent());
    }

    private List<AgonyResponse> from(List<Agony> content) {
        List<AgonyResponse> result = new ArrayList<>();
        for (Agony agony : content) {
            result.add(new AgonyResponse(agony.getId(), agony.getTitle(), agony.getHexColorCode()));
        }
        return result;
    }
}