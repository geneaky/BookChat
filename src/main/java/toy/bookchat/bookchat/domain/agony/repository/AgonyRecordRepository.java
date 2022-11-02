package toy.bookchat.bookchat.domain.agony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.agony.repository.query.AgonyRecordQueryRepository;

public interface AgonyRecordRepository extends AgonyRecordQueryRepository,
    JpaRepository<AgonyRecord, Long> {

}
