package toy.bookchat.bookchat.domain.agonyrecord.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecordTitleAndContent;

@Component
public class AgonyRecordManager {

    private final AgonyRecordRepository agonyRecordRepository;

    public AgonyRecordManager(AgonyRecordRepository agonyRecordRepository) {
        this.agonyRecordRepository = agonyRecordRepository;
    }

    public void modify(Long userId, Long bookShelfId, Long agonyId, Long recordId, AgonyRecordTitleAndContent agonyRecordTitleAndContent) {
        agonyRecordRepository.reviseAgonyRecord(bookShelfId, agonyId, recordId, userId, agonyRecordTitleAndContent.getTitle(), agonyRecordTitleAndContent.getContent());
    }
}
