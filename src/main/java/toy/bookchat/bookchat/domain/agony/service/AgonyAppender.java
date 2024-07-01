package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Component
public class AgonyAppender {

    private final AgonyRepository agonyRepository;

    public AgonyAppender(AgonyRepository agonyRepository) {
        this.agonyRepository = agonyRepository;
    }

    public Long append(Agony agony, BookShelf bookShelf) {
        AgonyEntity agonyEntity = AgonyEntity.builder()
            .title(agony.getTitle())
            .hexColorCode(agony.getHexColorCode())
            .bookShelfId(bookShelf.getId())
            .build();

        agonyRepository.save(agonyEntity);

        return agonyEntity.getId();
    }
}
