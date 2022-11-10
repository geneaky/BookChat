package toy.bookchat.bookchat.domain.agony.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.exception.agony.AgonyNotFoundException;

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
        CreateAgonyRecordRequest createAgonyRecordRequest = mock(
            CreateAgonyRecordRequest.class);
        Agony agony = mock(Agony.class);

        when(agonyRepository.findUserBookShelfAgony(any(), any(), any())).thenReturn(
            Optional.of(agony));

        agonyRecordService.storeAgonyRecord(createAgonyRecordRequest, 1L, 1L, 1L);

        verify(agonyRecordRepository).save(any());
    }

    @Test
    void 본인이_생성한_고민없이_고민기록_등록시도시_예외발생() throws Exception {
        CreateAgonyRecordRequest createAgonyRecordRequest = mock(
            CreateAgonyRecordRequest.class);

        assertThatThrownBy(() -> {
            agonyRecordService.storeAgonyRecord(createAgonyRecordRequest, 1L, 1L, 1L);
        }).isInstanceOf(AgonyNotFoundException.class);
    }

    @Test
    void 본인이_생성한_고민_조회_성공() throws Exception {

        AgonyRecord agonyRecord1 = AgonyRecord.builder()
            .id(1L)
            .title("title1")
            .content("content1")
            .build();
        agonyRecord1.setCreatedAt(LocalDateTime.now());
        AgonyRecord agonyRecord2 = AgonyRecord.builder()
            .id(2L)
            .title("title2")
            .content("content2")
            .build();
        agonyRecord2.setCreatedAt(LocalDateTime.now());

        List<AgonyRecord> list = List.of(agonyRecord1, agonyRecord2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<AgonyRecord> page = new PageImpl<>(list, pageable, list.size());
        when(agonyRecordRepository.findSliceOfUserAgonyRecords(any(), any(), any(),
            any(), any())).thenReturn(
            page);
        SliceOfAgonyRecordsResponse pageOfAgonyRecordsResponse = agonyRecordService.searchPageOfAgonyRecords(
            1L, 1L, 1L, pageable, Optional.empty());

        int result = pageOfAgonyRecordsResponse.getAgonyRecordResponseList().size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 본인이_생성한_고민_기록_삭제_성공() throws Exception {
        agonyRecordService.deleteAgonyRecord(1L, 1L, 1L, 1L);

        verify(agonyRecordRepository).deleteAgony(any(), any(), any(), any());
    }

    @Test
    void 본인이_생성한_고민_기록_수정_성공() throws Exception {
        ReviseAgonyRecordRequest reviseAgonyRecordRequest = ReviseAgonyRecordRequest.builder()
            .recordTitle("수정 제목")
            .recordContent("수정 내용")
            .build();

        agonyRecordService.reviseAgonyRecord(1L, 1L, 1L, 1L, reviseAgonyRecordRequest);

        verify(agonyRecordRepository).reviseAgonyRecord(any(), any(), any(), any(), any(), any());
    }
}