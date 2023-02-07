package toy.bookchat.bookchat.domain.agony.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agony.Agony;

public interface AgonyQueryRepository {

    Optional<Agony> findUserBookShelfAgony(Long bookShelfId, Long agonyId, Long userId);

    Slice<Agony> findUserBookShelfSliceOfAgonies(Long bookShelfId, Long userId, Pageable pageable,
        Optional<Long> postCursorId);

    void deleteByAgoniesIds(Long bookShelfId, Long userId, List<Long> agoniesIds);

    void deleteAllByUserId(Long userId);

    void deleteByBookShelfIdAndUserId(Long bookShelfId, Long userId);
}
