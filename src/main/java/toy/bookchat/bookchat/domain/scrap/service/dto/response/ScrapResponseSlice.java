package toy.bookchat.bookchat.domain.scrap.service.dto.response;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.common.CursorMeta;

@Getter
public class ScrapResponseSlice {

    private List<ScrapResponse> scrapResponseList;
    private CursorMeta<ScrapResponse, Long> cursorMeta;

    private ScrapResponseSlice(Slice<ScrapResponse> slice) {
        this.cursorMeta = new CursorMeta<>(slice, ScrapResponse::getScrapId);
        this.scrapResponseList = slice.getContent();
    }

    public static ScrapResponseSlice of(Slice<ScrapResponse> slice) {
        return new ScrapResponseSlice(slice);
    }
}
