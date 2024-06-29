package toy.bookchat.bookchat.db_module.agony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.query.AgonyQueryRepository;

public interface AgonyRepository extends AgonyQueryRepository, JpaRepository<AgonyEntity, Long> {

}
