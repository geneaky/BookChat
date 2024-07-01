package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.AgonyTitleAndColorCode;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyNotFoundException;

@Component
public class AgonyManager {

    private final AgonyRepository agonyRepository;

    public AgonyManager(AgonyRepository agonyRepository) {
        this.agonyRepository = agonyRepository;
    }

    public void modify(Long userId, Long bookShelfId, Long agonyId, AgonyTitleAndColorCode agonyTitleAndColorCode) {
        AgonyEntity agonyEntity = agonyRepository.findUserBookShelfAgony(bookShelfId, agonyId, userId).orElseThrow(AgonyNotFoundException::new);

        agonyEntity.changeTitle(agonyTitleAndColorCode.getTitle());
        agonyEntity.changeHexColorCode(agonyTitleAndColorCode.getHexColorCode());
    }
}
