package toy.bookchat.bookchat.domain.agony.service.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.api.v1.response.SliceOfAgoniesResponse;

class SliceOfAgoniesResponseTest {

    @Test
    void 조회결과가_없는경우_텅빈_contents와_cursorId_null반환() throws Exception {
        List<Agony> content = new ArrayList<>();
        PageRequest pageable = PageRequest.of(0, 3, Sort.by("id").descending());

        SliceImpl<Agony> agonies = new SliceImpl<>(content, pageable, false);
        SliceOfAgoniesResponse result = new SliceOfAgoniesResponse(agonies);

        assertAll(
            () -> assertThat(result.getAgonyResponseList()).isEmpty(),
            () -> assertThat(result.getCursorMeta().getNextCursorId()).isNull()
        );
    }
}