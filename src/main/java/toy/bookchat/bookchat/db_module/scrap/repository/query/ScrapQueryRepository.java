package toy.bookchat.bookchat.db_module.scrap.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.db_module.scrap.ScrapEntity;
import toy.bookchat.bookchat.domain.scrap.api.v1.response.ScrapResponse;

public interface ScrapQueryRepository {

  Slice<ScrapResponse> findScraps(Long bookShelfId, Long postCursorId, Pageable pageable, Long userId);

  Optional<ScrapEntity> findUserScrap(Long scrapId, Long userId);
}
