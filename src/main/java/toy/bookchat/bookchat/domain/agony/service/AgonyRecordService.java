package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.exception.AgonyNotFoundException;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.user.User;

@Service
public class AgonyRecordService {

    private final AgonyRecordRepository agonyRecordRepository;
    private final AgonyRepository agonyRepository;

    public AgonyRecordService(AgonyRecordRepository agonyRecordRepository,
        AgonyRepository agonyRepository) {
        this.agonyRecordRepository = agonyRecordRepository;
        this.agonyRepository = agonyRepository;
    }

    public void storeAgonyRecord(CreateAgonyRecordRequestDto createAgonyRecordRequestDto, User user,
        Long bookId, Long agonyId) {
        Agony agony = agonyRepository.findUserBookShelfAgony(user.getId(), bookId, agonyId)
            .orElseThrow(() -> {
                throw new AgonyNotFoundException("Agony is not registered");
            });
        agonyRecordRepository.save(createAgonyRecordRequestDto.generateAgonyRecord(agony));
    }
}
