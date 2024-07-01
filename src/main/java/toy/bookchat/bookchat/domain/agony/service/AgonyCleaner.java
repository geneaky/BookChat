package toy.bookchat.bookchat.domain.agony.service;

import java.util.List;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;

@Component
public class AgonyCleaner {

    private final AgonyRepository agonyRepository;

    public AgonyCleaner(AgonyRepository agonyRepository) {
        this.agonyRepository = agonyRepository;
    }

    public void clean(Long userId, Long bookShelfId, List<Long> agoniesIds) {
        agonyRepository.deleteByAgoniesIds(bookShelfId, userId, agoniesIds);
    }
}
