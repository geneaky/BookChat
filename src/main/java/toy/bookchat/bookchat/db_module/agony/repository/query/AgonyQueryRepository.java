package toy.bookchat.bookchat.db_module.agony.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;

public interface AgonyQueryRepository {

    Optional<AgonyEntity> findUserBookShelfAgony(Long bookShelfId, Long agonyId, Long userId);

    Slice<AgonyEntity> findUserBookShelfSliceOfAgonies(Long bookShelfId, Long userId, Pageable pageable,
        Long postCursorId);

    void deleteByAgoniesIds(Long bookShelfId, Long userId, List<Long> agoniesIds);

    void deleteAllByUserId(Long userId);

    void deleteByBookShelfIdAndUserId(Long bookShelfId, Long userId);
}
