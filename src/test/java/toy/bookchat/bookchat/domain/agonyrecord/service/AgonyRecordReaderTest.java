package toy.bookchat.bookchat.domain.agonyrecord.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.agonyrecord.AgonyRecordEntity;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agonyrecord.AgonyRecord;
import toy.bookchat.bookchat.exception.notfound.agony.AgonyRecordNotFoundException;

@ExtendWith(MockitoExtension.class)
class AgonyRecordReaderTest {

    @Mock
    private AgonyRecordRepository agonyRecordRepository;
    @InjectMocks
    private AgonyRecordReader agonyRecordReader;

    @Test
    void 고민기록_다_건_조회_성공() throws Exception {
        AgonyRecordEntity agonyRecordEntity1 = AgonyRecordEntity.builder()
            .id(1L)
            .title("title1")
            .content("content1")
            .build();
        AgonyRecordEntity agonyRecordEntity2 = AgonyRecordEntity.builder()
            .id(2L)
            .title("title2")
            .content("content2")
            .build();
        List<AgonyRecordEntity> list = List.of(agonyRecordEntity1, agonyRecordEntity2);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        Slice<AgonyRecordEntity> slice = new SliceImpl<>(list, pageable, true);
        given(agonyRecordRepository.findSliceOfUserAgonyRecords(any(), any(), any(), any(), any())).willReturn(slice);

        Slice<AgonyRecord> slicedAgonyRecord = agonyRecordReader.readSlicedAgonyRecord(1L, 1L, 1L, mock(Pageable.class), 1L);

        assertThat(slicedAgonyRecord).hasSize(2).extracting(AgonyRecord::getId, AgonyRecord::getTitle, AgonyRecord::getContent)
            .containsExactly(
                tuple(1L, "title1", "content1"),
                tuple(2L, "title2", "content2")
            );
    }

    @Test
    void 고민기록_단_건_조회_성공() throws Exception {
        AgonyRecordEntity agonyRecordEntity = AgonyRecordEntity.builder()
            .id(1L)
            .title("title")
            .content("content")
            .build();
        given(agonyRecordRepository.findUserAgonyRecord(any(), any(), any(), any())).willReturn(Optional.of(agonyRecordEntity));
        AgonyRecord agonyRecord = agonyRecordReader.readAgonyRecord(1L, 1L, 1L, 1L);

        AgonyRecord expected = AgonyRecord.builder()
            .id(1L)
            .title("title")
            .content("content")
            .build();

        assertThat(agonyRecord).isEqualTo(expected);
    }

    @Test
    void 고민기록이_없는_경우_조회_실패() throws Exception {
        assertThatThrownBy(() -> agonyRecordReader.readAgonyRecord(1L, 1L, 1L, 1L)).isInstanceOf(AgonyRecordNotFoundException.class);
    }
}