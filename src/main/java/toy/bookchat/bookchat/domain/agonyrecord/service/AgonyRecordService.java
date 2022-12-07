package toy.bookchat.bookchat.domain.agonyrecord.service;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.exception.agony.AgonyNotFoundException;

@Service
public class AgonyRecordService {

    private final AgonyRecordRepository agonyRecordRepository;
    private final AgonyRepository agonyRepository;

    public AgonyRecordService(AgonyRecordRepository agonyRecordRepository,
        AgonyRepository agonyRepository) {
        this.agonyRecordRepository = agonyRecordRepository;
        this.agonyRepository = agonyRepository;
    }

    @Transactional
    public void storeAgonyRecord(CreateAgonyRecordRequest createAgonyRecordRequest,
        Long userId, Long agonyId) {
        Agony agony = agonyRepository.findUserBookShelfAgony(userId, agonyId)
            .orElseThrow(AgonyNotFoundException::new);
        agonyRecordRepository.save(createAgonyRecordRequest.generateAgonyRecord(agony));
    }

    @Transactional(readOnly = true)
    public SliceOfAgonyRecordsResponse searchPageOfAgonyRecords(Long agonyId,
        Long userId, Pageable pageable, Optional<Long> postCursorId) {
        Slice<AgonyRecord> agonyRecordSlice = agonyRecordRepository.findSliceOfUserAgonyRecords(
            agonyId, userId, pageable, postCursorId);
        return new SliceOfAgonyRecordsResponse(agonyRecordSlice);
    }

    @Transactional
    public void deleteAgonyRecord(Long agonyId, Long recordId, Long userId) {
        agonyRecordRepository.deleteAgony(userId, agonyId, recordId);
    }

    @Transactional
    public void reviseAgonyRecord(Long agonyId, Long recordId, Long userId,
        ReviseAgonyRecordRequest reviseAgonyRecordRequest) {
        agonyRecordRepository.reviseAgonyRecord(userId, agonyId, recordId,
            reviseAgonyRecordRequest.getRecordTitle(), reviseAgonyRecordRequest.getRecordContent());
    }
}
