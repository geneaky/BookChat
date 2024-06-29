package toy.bookchat.bookchat.domain.agonyrecord.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.request.CreateAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.request.ReviseAgonyRecordRequest;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.response.AgonyRecordResponse;
import toy.bookchat.bookchat.domain.agonyrecord.api.v1.response.SliceOfAgonyRecordsResponse;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyNotFoundException;

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
        CreateAgonyRecordRequest createAgonyRecordRequest = mock(CreateAgonyRecordRequest.class);
        AgonyEntity agonyEntity = mock(AgonyEntity.class);
        AgonyRecordEntity agonyRecordEntity = AgonyRecordEntity.builder().build();

        given(agonyRepository.findUserBookShelfAgony(any(), any(), any())).willReturn(Optional.of(agonyEntity));
        given(createAgonyRecordRequest.generateAgonyRecord(eq(agonyEntity))).willReturn(agonyRecordEntity);

        agonyRecordService.storeAgonyRecord(1L, createAgonyRecordRequest, 1L, 1L);

        verify(agonyRecordRepository).save(any());
    }

    @Test
    void 본인이_생성한_고민없이_고민기록_등록시도시_예외발생() throws Exception {
        CreateAgonyRecordRequest createAgonyRecordRequest = mock(
            CreateAgonyRecordRequest.class);

        assertThatThrownBy(() -> {
            agonyRecordService.storeAgonyRecord(1L, createAgonyRecordRequest, 1L, 1L);
        }).isInstanceOf(AgonyNotFoundException.class);
    }

    @Test
    void 본인이_생성한_고민_조회_성공() throws Exception {

        AgonyRecordEntity agonyRecordEntity1 = AgonyRecordEntity.builder()
            .id(1L)
            .title("title1")
            .content("content1")
            .build();
        agonyRecordEntity1.setCreatedAt(LocalDateTime.now());
        AgonyRecordEntity agonyRecordEntity2 = AgonyRecordEntity.builder()
            .id(2L)
            .title("title2")
            .content("content2")
            .build();
        agonyRecordEntity2.setCreatedAt(LocalDateTime.now());

        List<AgonyRecordEntity> list = List.of(agonyRecordEntity1, agonyRecordEntity2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<AgonyRecordEntity> page = new PageImpl<>(list, pageable, list.size());
        when(agonyRecordRepository.findSliceOfUserAgonyRecords(any(), any(), any(),
            any(), any())).thenReturn(
            page);
        SliceOfAgonyRecordsResponse pageOfAgonyRecordsResponse = agonyRecordService.searchPageOfAgonyRecords(
            1L, 1L, 1L, pageable, null);

        int result = pageOfAgonyRecordsResponse.getAgonyRecordResponseList().size();
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 사용자_고민_기록_단_건_조회_성공() throws Exception {
        AgonyRecordEntity agonyRecordEntity = AgonyRecordEntity.builder()
            .id(1L)
            .title("title1")
            .content("content1")
            .build();
        agonyRecordEntity.setCreatedAt(LocalDateTime.now());
        given(agonyRecordRepository.findUserAgonyRecord(any(), any(), any(), any())).willReturn(Optional.ofNullable(agonyRecordEntity));

        AgonyRecordResponse actual = agonyRecordService.searchAgonyRecord(1L, 1L, 1L, 1L);

        AgonyRecordResponse expected = AgonyRecordResponse.from(agonyRecordEntity);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 본인이_생성한_고민_기록_삭제_성공() throws Exception {
        agonyRecordService.deleteAgonyRecord(1L, 1L, 1L, 1L);

        verify(agonyRecordRepository).deleteAgonyRecord(any(), any(), any(), any());
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