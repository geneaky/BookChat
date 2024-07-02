package toy.bookchat.bookchat.domain.agonyrecord.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.domain.agony.service.AgonyReader;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecordTitleAndContent;

@ExtendWith(MockitoExtension.class)
class AgonyRecordServiceTest {

    @Mock
    private AgonyReader agonyReader;
    @Mock
    private AgonyRecordAppender agonyRecordAppender;
    @Mock
    private AgonyRecordReader agonyRecordReader;
    @Mock
    private AgonyRecordCleaner agonyRecordCleaner;
    @Mock
    private AgonyRecordManager agonyRecordManager;
    @InjectMocks
    private AgonyRecordService agonyRecordService;

    @Test
    void 고민에_고민기록_등록_성공() throws Exception {
        AgonyRecord agonyRecord = AgonyRecord.builder().build();
        agonyRecordService.storeAgonyRecord(1L, agonyRecord, 1L, 1L);

        verify(agonyReader).readAgony(any(), any(), any());
        verify(agonyRecordAppender).append(any(), any());
    }

    @Test
    void 본인이_생성한_고민_조회_성공() throws Exception {
        Pageable pageable = mock(Pageable.class);
        agonyRecordService.searchPageOfAgonyRecords(1L, 1L, 1L, pageable, null);

        verify(agonyRecordReader).readSlicedAgonyRecord(any(), any(), any(), any(), any());
    }

    @Test
    void 사용자_고민_기록_단_건_조회_성공() throws Exception {
        agonyRecordService.searchAgonyRecord(1L, 1L, 1L, 1L);
        verify(agonyRecordReader).readAgonyRecord(any(), any(), any(), any());
    }

    @Test
    void 본인이_생성한_고민_기록_삭제_성공() throws Exception {
        agonyRecordService.deleteAgonyRecord(1L, 1L, 1L, 1L);

        verify(agonyRecordCleaner).clean(any(), any(), any(), any());
    }

    @Test
    void 본인이_생성한_고민_기록_수정_성공() throws Exception {
        AgonyRecordTitleAndContent agonyRecordTitleAndContent = AgonyRecordTitleAndContent.builder()
            .title("수정 제목")
            .content("수정 내용")
            .build();
        agonyRecordService.reviseAgonyRecord(1L, 1L, 1L, 1L, agonyRecordTitleAndContent);

        verify(agonyRecordManager).modify(any(), any(), any(), any(), any());
    }
}