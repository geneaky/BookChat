package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;

@Component
public class AgonyAppender {

    private final AgonyRepository agonyRepository;

    public AgonyAppender(AgonyRepository agonyRepository) {
        this.agonyRepository = agonyRepository;
    }

    public void append(Agony agony, BookShelfEntity bookShelfEntity) {
        AgonyEntity agonyEntity = AgonyEntity.builder()
            .title(agony.getTitle())
            .hexColorCode(agony.getHexColorCode())
            .bookShelfEntity(bookShelfEntity)
            .build();

        agonyRepository.save(agonyEntity);

        agony.setId(agonyEntity.getId());
    }
}
