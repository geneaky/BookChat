package toy.bookchat.bookchat.domain.agonyrecord.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.repository.query.AgonyRecordQueryRepository;

public interface AgonyRecordRepository extends AgonyRecordQueryRepository,
    JpaRepository<AgonyRecord, Long> {

    void deleteByAgony(Agony agony);
}
