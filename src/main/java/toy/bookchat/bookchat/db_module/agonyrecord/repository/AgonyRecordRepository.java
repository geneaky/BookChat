package toy.bookchat.bookchat.db_module.agonyrecord.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.query.AgonyRecordQueryRepository;

public interface AgonyRecordRepository extends AgonyRecordQueryRepository, JpaRepository<AgonyRecordEntity, Long> {

}
