package toy.bookchat.bookchat.domain.agony.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agony.Agony;

public interface AgonyQueryRepository {

    Optional<Agony> findUserBookShelfAgony(Long userId, Long agonyId);

    Slice<Agony> findUserBookShelfSliceOfAgonies(long userId, Pageable pageable,
        Optional<Long> postAgonyCursorId);

    void deleteByAgoniesIds(Long userId, List<Long> agoniesIds);
}
