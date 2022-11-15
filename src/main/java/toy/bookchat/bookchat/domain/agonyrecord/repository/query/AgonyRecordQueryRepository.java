package toy.bookchat.bookchat.domain.agonyrecord.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;

public interface AgonyRecordQueryRepository {

    Slice<AgonyRecord> findSliceOfUserAgonyRecords(Long agonyId, Long userId,
        Pageable pageable, Optional<Long> postRecordCursorId);
    
    void deleteAgony(Long userId, Long agonyId, Long recordId);

    void reviseAgonyRecord(Long userId, Long agonyId, Long recordId,
        String recordTitle, String recordContent);

    void deleteByAgoniesIds(Long userId, List<Long> agoniesIds);
}
