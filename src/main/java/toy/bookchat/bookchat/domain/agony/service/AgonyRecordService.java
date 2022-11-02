package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.agony.exception.AgonyNotFoundException;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.response.PageOfAgonyRecordsResponse;

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
    public void storeAgonyRecord(CreateAgonyRecordRequestDto createAgonyRecordRequestDto,
        Long userId,
        Long bookId, Long agonyId) {
        Agony agony = agonyRepository.findUserBookShelfAgony(userId, bookId, agonyId)
            .orElseThrow(() -> {
                throw new AgonyNotFoundException("Agony is not registered");
            });
        agonyRecordRepository.save(createAgonyRecordRequestDto.generateAgonyRecord(agony));
    }

    @Transactional(readOnly = true)
    public PageOfAgonyRecordsResponse searchPageOfAgonyRecords(Long bookId, Long agonyId,
        Long userId, Pageable pageable) {
        Page<AgonyRecord> agonyRecordPage = agonyRecordRepository.findPageOfUserAgonyRecords(bookId,
            agonyId, userId, pageable);
        return new PageOfAgonyRecordsResponse(agonyRecordPage);
    }
}
