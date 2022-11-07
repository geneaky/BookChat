package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.BasePageOfAgonyRecordsResponse;
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
        Long userId,
        Long bookId, Long agonyId) {
        Agony agony = agonyRepository.findUserBookShelfAgony(userId, bookId, agonyId)
            .orElseThrow(AgonyNotFoundException::new);
        agonyRecordRepository.save(createAgonyRecordRequest.generateAgonyRecord(agony));
    }

    @Transactional(readOnly = true)
    public BasePageOfAgonyRecordsResponse searchPageOfAgonyRecords(Long bookId, Long agonyId,
        Long userId, Pageable pageable) {
        Page<AgonyRecord> agonyRecordPage = agonyRecordRepository.findPageOfUserAgonyRecords(bookId,
            agonyId, userId, pageable);
        return new BasePageOfAgonyRecordsResponse(agonyRecordPage);
    }

    @Transactional
    public void deleteAgonyRecord(Long bookId, Long agonyId, Long recordId, Long userId) {
        agonyRecordRepository.deleteAgony(userId, bookId, agonyId, recordId);
    }

    @Transactional
    public void reviseAgonyRecord(Long bookId, Long agonyId, Long recordId, Long userId,
        ReviseAgonyRecordRequest reviseAgonyRecordRequest) {
        agonyRecordRepository.reviseAgonyRecord(userId, bookId, agonyId, recordId,
            reviseAgonyRecordRequest.getRecordTitle(), reviseAgonyRecordRequest.getRecordContent());
    }
}
