package toy.bookchat.bookchat.domain.agonyrecord.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;

@ExtendWith(MockitoExtension.class)
class AgonyRecordCleanerTest {

    @Mock
    private AgonyRecordRepository agonyRecordRepository;
    @InjectMocks
    private AgonyRecordCleaner agonyRecordCleaner;

    @Test
    void 고민_기록_삭제_성공() throws Exception {
        agonyRecordCleaner.clean(1L, 1L, List.of(1L, 2L, 3L));

        verify(agonyRecordRepository).deleteByAgoniesIds(any(), any(), any());
    }
}