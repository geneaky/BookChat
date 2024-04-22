package toy.bookchat.bookchat.domain.scrap.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.scrap.Scrap;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponse;

public interface ScrapQueryRepository {

    Slice<ScrapResponse> findScraps(Long bookShelfId, Long postCursorId, Pageable pageable, Long userId);

    Optional<Scrap> findUserScrap(Long scrapId, Long userId);
}
