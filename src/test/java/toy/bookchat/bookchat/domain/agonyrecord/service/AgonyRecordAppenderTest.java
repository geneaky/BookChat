package toy.bookchat.bookchat.domain.agonyrecord.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;

@ExtendWith(MockitoExtension.class)
class AgonyRecordAppenderTest {

    @Mock
    private AgonyRecordRepository agonyRecordRepository;
    @InjectMocks
    private AgonyRecordAppender agonyRecordAppender;

    @Test
    void 고민에_고민기록_추가_성공() throws Exception {
        Agony agony = Agony.builder()
            .id(1L)
            .build();
        AgonyRecord agonyRecord = AgonyRecord.builder()
            .title("QpqG5wCewD")
            .content("f2WGgVjdFYp")
            .build();

        agonyRecordAppender.append(agony, agonyRecord);

        verify(agonyRecordRepository).save(any());
    }
}