package toy.bookchat.bookchat.domain.agonyrecord.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyRecordNotFoundException;

@Component
public class AgonyRecordReader {

    private final AgonyRecordRepository agonyRecordRepository;

    public AgonyRecordReader(AgonyRecordRepository agonyRecordRepository) {
        this.agonyRecordRepository = agonyRecordRepository;
    }

    public Slice<AgonyRecord> readSlicedAgonyRecord(Long userId, Long bookShelfId, Long agonyId, Pageable pageable, Long postCursorId) {
        Slice<AgonyRecordEntity> agonyRecordSlice = agonyRecordRepository.findSliceOfUserAgonyRecords(bookShelfId, agonyId, userId, pageable, postCursorId);
        return agonyRecordSlice.map(agonyRecordEntity -> AgonyRecord.builder()
            .id(agonyRecordEntity.getId())
            .title(agonyRecordEntity.getTitle())
            .content(agonyRecordEntity.getContent())
            .createdAt(agonyRecordEntity.getCreatedAt())
            .build());
    }

    public AgonyRecord readAgonyRecord(Long userId, Long bookShelfId, Long agonyId, Long agonyRecordId) {
        AgonyRecordEntity agonyRecordEntity = agonyRecordRepository.findUserAgonyRecord(bookShelfId, agonyId, agonyRecordId, userId).orElseThrow(AgonyRecordNotFoundException::new);

        return AgonyRecord.builder()
            .id(agonyRecordEntity.getId())
            .title(agonyRecordEntity.getTitle())
            .content(agonyRecordEntity.getContent())
            .createdAt(agonyRecordEntity.getCreatedAt())
            .build();
    }
}
