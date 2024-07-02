package toy.bookchat.bookchat.domain.agonyrecord.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;

@Component
public class AgonyRecordAppender {

    private final AgonyRecordRepository agonyRecordRepository;

    public AgonyRecordAppender(AgonyRecordRepository agonyRecordRepository) {
        this.agonyRecordRepository = agonyRecordRepository;
    }


    public Long append(Agony agony, AgonyRecord agonyRecord) {
        AgonyRecordEntity agonyRecordEntity = AgonyRecordEntity.builder()
            .agonyId(agony.getId())
            .title(agonyRecord.getTitle())
            .content(agonyRecord.getContent())
            .build();
        agonyRecordRepository.save(agonyRecordEntity);

        return agonyRecordEntity.getId();
    }
}
