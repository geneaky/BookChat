package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.exception.AgonyNotFoundException;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.PageOfAgonyRecordsResponse;

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
        Long userId) {
        return null;
    }
}
