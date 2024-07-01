package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyNotFoundException;

@Component
public class AgonyReader {

    private final AgonyRepository agonyRepository;

    public AgonyReader(AgonyRepository agonyRepository) {
        this.agonyRepository = agonyRepository;
    }

    public Agony readAgony(Long userId, Long bookShelfId, Long agonyId) {
        AgonyEntity agonyEntity = agonyRepository.findUserBookShelfAgony(bookShelfId, agonyId, userId).orElseThrow(AgonyNotFoundException::new);

        return Agony.builder()
            .id(agonyEntity.getId())
            .title(agonyEntity.getTitle())
            .hexColorCode(agonyEntity.getHexColorCode())
            .build();
    }

    public Slice<Agony> readSlicedAgony(Long userId, Long bookShelfId, Pageable pageable, Long postCursorId) {
        Slice<AgonyEntity> slicedAgonyEntities = agonyRepository.findUserBookShelfSliceOfAgonies(bookShelfId, userId, pageable, postCursorId);

        return slicedAgonyEntities.map(agonyEntity -> Agony.builder()
            .id(agonyEntity.getId())
            .title(agonyEntity.getTitle())
            .hexColorCode(agonyEntity.getHexColorCode())
            .build());
    }
}
