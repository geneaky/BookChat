package toy.bookchat.bookchat.domain.agony.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;

@ExtendWith(MockitoExtension.class)
class AgonyReaderTest {

    @Mock
    private AgonyRepository agonyRepository;
    @InjectMocks
    private AgonyReader agonyReader;

    @Test
    void 사용자_고민_상세_조회_성공() throws Exception {
        given(agonyRepository.findUserBookShelfAgony(any(), any(), any())).willReturn(Optional.of(mock(AgonyEntity.class)));

        agonyReader.readAgony(1L, 1L, 1L);

        verify(agonyRepository).findUserBookShelfAgony(any(), any(), any());
    }

    @Test
    void 사용자_고민_페이징_조회_성공() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());

        AgonyEntity agonyEntity1 = AgonyEntity.builder()
            .id(1L)
            .title("agony1")
            .hexColorCode("blue")
            .build();
        AgonyEntity agonyEntity2 = AgonyEntity.builder()
            .id(2L)
            .title("agony2")
            .hexColorCode("red")
            .build();
        List<AgonyEntity> contents = List.of(agonyEntity1, agonyEntity2);

        given(agonyRepository.findUserBookShelfSliceOfAgonies(any(), any(), any(), any())).willReturn(new SliceImpl<>(contents, pageRequest, true));

        agonyReader.readSlicedAgony(1L, 1L, pageRequest, 1L);

        verify(agonyRepository).findUserBookShelfSliceOfAgonies(any(), any(), any(), any());
    }
}