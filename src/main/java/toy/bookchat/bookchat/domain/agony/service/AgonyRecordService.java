package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.AgonyRecordResponse;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyNotFoundException;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyRecordNotFoundException;

@Service
public class AgonyRecordService {

    private final AgonyRecordRepository agonyRecordRepository;
    private final AgonyRepository agonyRepository;

    public AgonyRecordService(AgonyRecordRepository agonyRecordRepository, AgonyRepository agonyRepository) {
        this.agonyRecordRepository = agonyRecordRepository;
        this.agonyRepository = agonyRepository;
    }

    @Transactional
    public Long storeAgonyRecord(Long bookShelfId, CreateAgonyRecordRequest createAgonyRecordRequest, Long userId, Long agonyId) {
        Agony agony = agonyRepository.findUserBookShelfAgony(bookShelfId, agonyId, userId)
            .orElseThrow(AgonyNotFoundException::new);
        AgonyRecord agonyRecord = createAgonyRecordRequest.generateAgonyRecord(agony);
        agonyRecordRepository.save(agonyRecord);

        return agonyRecord.getId();
    }

    @Transactional(readOnly = true)
    public SliceOfAgonyRecordsResponse searchPageOfAgonyRecords(Long bookShelfId, Long agonyId, Long userId, Pageable pageable, Long postCursorId) {
        Slice<AgonyRecord> agonyRecordSlice = agonyRecordRepository.findSliceOfUserAgonyRecords(bookShelfId, agonyId, userId, pageable, postCursorId);
        return new SliceOfAgonyRecordsResponse(agonyRecordSlice);
    }

    @Transactional
    public void deleteAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId) {
        agonyRecordRepository.deleteAgonyRecord(bookShelfId, agonyId, recordId, userId);
    }

    @Transactional
    public void reviseAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId, ReviseAgonyRecordRequest reviseAgonyRecordRequest) {
        agonyRecordRepository.reviseAgonyRecord(bookShelfId, agonyId, recordId, userId, reviseAgonyRecordRequest.getRecordTitle(), reviseAgonyRecordRequest.getRecordContent());
    }

    @Transactional(readOnly = true)
    public AgonyRecordResponse searchAgonyRecord(Long bookShelfId, Long agonyId, Long recordId, Long userId) {
        AgonyRecord agonyRecord = agonyRecordRepository.findUserAgonyRecord(bookShelfId, agonyId, recordId, userId).orElseThrow(AgonyRecordNotFoundException::new);
        return AgonyRecordResponse.from(agonyRecord);
    }
}
