package toy.bookchat.bookchat.domain.agony.repository;

import java.util.Optional;
import toy.bookchat.bookchat.domain.agony.Agony;

public interface AgonyQueryRepository {

    Optional<Agony> findUserBookShelfAgony(Long userId, Long bookId, Long agonyId);
}
