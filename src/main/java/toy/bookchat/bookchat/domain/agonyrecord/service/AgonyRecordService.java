package toy.bookchat.bookchat.domain.agonyrecord.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.service.AgonyReader;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecordTitleAndContent;

@Service
public class AgonyRecordService {

    private final AgonyReader agonyReader;
    private final AgonyRecordAppender agonyRecordAppender;
    private final AgonyRecordReader agonyRecordReader;
    private final AgonyRecordCleaner agonyRecordCleaner;
    private final AgonyRecordManager agonyRecordManager;

    public AgonyRecordService(AgonyReader agonyReader, AgonyRecordAppender agonyRecordAppender, AgonyRecordReader agonyRecordReader, AgonyRecordCleaner agonyRecordCleaner,
        AgonyRecordManager agonyRecordManager) {
        this.agonyReader = agonyReader;
        this.agonyRecordAppender = agonyRecordAppender;
        this.agonyRecordReader = agonyRecordReader;
        this.agonyRecordCleaner = agonyRecordCleaner;
        this.agonyRecordManager = agonyRecordManager;
    }

    @Transactional
    public Long storeAgonyRecord(Long bookShelfId, AgonyRecord agonyRecord, Long userId, Long agonyId) {
        Agony agony = agonyReader.readAgony(userId, bookShelfId, agonyId);
        Long agonyRecordId = agonyRecordAppender.append(agony, agonyRecord);

        return agonyRecordId;
    }

    @Transactional(readOnly = true)
    public Slice<AgonyRecord> searchPageOfAgonyRecords(Long bookShelfId, Long agonyId, Long userId, Pageable pageable, Long postCursorId) {
        Slice<AgonyRecord> agonyRecordSlice = agonyRecordReader.readSlicedAgonyRecord(userId, bookShelfId, agonyId, pageable, postCursorId);
        return agonyRecordSlice;
    }

    @Transactional(readOnly = true)
    public AgonyRecord searchAgonyRecord(Long bookShelfId, Long agonyId, Long agonyRecordId, Long userId) {
        AgonyRecord agonyRecord = agonyRecordReader.readAgonyRecord(userId, bookShelfId, agonyId, agonyRecordId);

        return agonyRecord;
    }

    @Transactional
    public void deleteAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId) {
        agonyRecordCleaner.clean(userId, bookShelfId, agonyId, recordId);
    }

    @Transactional
    public void reviseAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId, AgonyRecordTitleAndContent agonyRecordTitleAndContent) {
        agonyRecordManager.modify(userId, bookShelfId, agonyId, recordId, agonyRecordTitleAndContent);
    }
}
