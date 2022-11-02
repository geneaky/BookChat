package toy.bookchat.bookchat.domain.agony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.repository.query.AgonyQueryRepository;

public interface AgonyRepository extends AgonyQueryRepository, JpaRepository<Agony, Long> {

}
