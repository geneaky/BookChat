package toy.bookchat.bookchat.domain.agony.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;

@ExtendWith(MockitoExtension.class)
class AgonyCleanerTest {

    @Mock
    private AgonyRepository agonyRepository;
    @InjectMocks
    private AgonyCleaner agonyCleaner;

    @Test
    void 고민_삭제_성공() throws Exception {
        agonyCleaner.clean(1L, 1L, List.of(1L, 2L, 3L));

        verify(agonyRepository).deleteByAgoniesIds(any(), any(), any());
    }
}