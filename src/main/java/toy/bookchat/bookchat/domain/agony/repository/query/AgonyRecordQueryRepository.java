package toy.bookchat.bookchat.domain.agony.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;

public interface AgonyRecordQueryRepository {

    Page<AgonyRecord> findPageOfUserAgonyRecords(Long bookId, Long agonyId, Long userId,
        Pageable pageable);


    void deleteAgony(Long userId, Long bookId, Long agonyId, Long recordId);

    void reviseAgonyRecord(Long userId, Long bookId, Long agonyId, Long recordId,
        String recordTitle, String recordContent);
}
