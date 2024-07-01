package toy.bookchat.bookchat.domain.agonyrecord.service;

import java.util.List;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;

@Component
public class AgonyRecordCleaner {

    private final AgonyRecordRepository agonyRecordRepository;

    public AgonyRecordCleaner(AgonyRecordRepository agonyRecordRepository) {
        this.agonyRecordRepository = agonyRecordRepository;
    }

    public void clean(Long userId, Long bookShelfId, List<Long> agoniesIds) {
        agonyRecordRepository.deleteByAgoniesIds(bookShelfId, userId, agoniesIds);
    }
}
