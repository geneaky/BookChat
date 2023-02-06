package toy.bookchat.bookchat.domain.agonyrecord.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;

public interface AgonyRecordQueryRepository {

    Slice<AgonyRecord> findSliceOfUserAgonyRecords(Long bookShelfId, Long agonyId, Long userId,
        Pageable pageable, Optional<Long> postCursorId);

    void deleteAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId);

    void reviseAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId,
        String recordTitle, String recordContent);

    void deleteByAgoniesIds(Long bookShelfId, Long userId, List<Long> agoniesIds);

    void deleteAllByUserId(Long userId);
}
