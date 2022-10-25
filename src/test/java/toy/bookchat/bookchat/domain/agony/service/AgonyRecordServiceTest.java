package toy.bookchat.bookchat.domain.agony.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.exception.AgonyNotFoundException;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.user.User;

@ExtendWith(MockitoExtension.class)
class AgonyRecordServiceTest {

    @Mock
    AgonyRecordRepository agonyRecordRepository;
    @Mock
    AgonyRepository agonyRepository;
    @InjectMocks
    AgonyRecordService agonyRecordService;

    @Test
    void 고민에_고민기록_등록_성공() throws Exception {
        User user = mock(User.class);
        CreateAgonyRecordRequestDto createAgonyRecordRequestDto = mock(
            CreateAgonyRecordRequestDto.class);
        Agony agony = mock(Agony.class);

        when(agonyRepository.findUserBookShelfAgony(any(), any(), any())).thenReturn(
            Optional.of(agony));

        agonyRecordService.storeAgonyRecord(createAgonyRecordRequestDto, user, 1L, 1L);

        verify(agonyRecordRepository).save(any());
    }

    @Test
    void 본인이_생성한_고민없이_고민기록_등록시도시_예외발생() throws Exception {
        User user = mock(User.class);
        CreateAgonyRecordRequestDto createAgonyRecordRequestDto = mock(
            CreateAgonyRecordRequestDto.class);

        assertThatThrownBy(() -> {
            agonyRecordService.storeAgonyRecord(createAgonyRecordRequestDto, user, 1L, 1L);
        }).isInstanceOf(AgonyNotFoundException.class);
    }
}