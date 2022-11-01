package toy.bookchat.bookchat.domain.agony.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.AgonyRecord;
import toy.bookchat.bookchat.domain.agony.exception.AgonyNotFoundException;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.PageOfAgonyRecordsResponse;

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
        CreateAgonyRecordRequestDto createAgonyRecordRequestDto = mock(
            CreateAgonyRecordRequestDto.class);
        Agony agony = mock(Agony.class);

        when(agonyRepository.findUserBookShelfAgony(any(), any(), any())).thenReturn(
            Optional.of(agony));

        agonyRecordService.storeAgonyRecord(createAgonyRecordRequestDto, 1L, 1L, 1L);

        verify(agonyRecordRepository).save(any());
    }

    @Test
    void 본인이_생성한_고민없이_고민기록_등록시도시_예외발생() throws Exception {
        CreateAgonyRecordRequestDto createAgonyRecordRequestDto = mock(
            CreateAgonyRecordRequestDto.class);

        assertThatThrownBy(() -> {
            agonyRecordService.storeAgonyRecord(createAgonyRecordRequestDto, 1L, 1L, 1L);
        }).isInstanceOf(AgonyNotFoundException.class);
    }

    @Test
    void 본인이_생성한_고민_조회_성공() throws Exception {

        AgonyRecord agonyRecord1 = mock(AgonyRecord.class);
        AgonyRecord agonyRecord2 = mock(AgonyRecord.class);

        when(agonyRecord1.getId()).thenReturn(1L);
        when(agonyRecord1.getTitle()).thenReturn("title1");
        when(agonyRecord1.getContent()).thenReturn("content1");
        when(agonyRecord1.getCreateTimeInYearMonthDayFormat()).thenReturn("2022-11-01");
        when(agonyRecord2.getId()).thenReturn(2L);
        when(agonyRecord2.getTitle()).thenReturn("title2");
        when(agonyRecord2.getContent()).thenReturn("content2");
        when(agonyRecord2.getCreateTimeInYearMonthDayFormat()).thenReturn("2022-11-01");

        List<AgonyRecord> list = List.of(agonyRecord1, agonyRecord2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<AgonyRecord> page = new PageImpl<>(list, pageable, list.size());
        when(agonyRecordRepository.findPageOfUserAgonyRecords(any(), any(), any())).thenReturn(
            page);
        PageOfAgonyRecordsResponse pageOfAgonyRecordsResponse = agonyRecordService.searchPageOfAgonyRecords(
            1L, 1L, 1L);

        int result = pageOfAgonyRecordsResponse.getAgonyRecordResponseList().size();
        assertThat(result).isEqualTo(2);
    }
}