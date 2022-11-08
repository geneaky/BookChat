package toy.bookchat.bookchat.domain.agony.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.agony.Agony;

public interface AgonyQueryRepository {

    Optional<Agony> findUserBookShelfAgony(Long userId, Long bookId, Long agonyId);

    Page<Agony> findUserBookShelfPageOfAgonies(long bookId, long userId, Pageable pageable);

    void deleteByAgoniesIds(Long bookId, Long userId, List<Long> agoniesIds);
}
