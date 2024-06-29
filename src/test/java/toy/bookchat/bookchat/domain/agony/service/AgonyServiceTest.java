package toy.bookchat.bookchat.domain.agony.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.api.v1.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.api.v1.response.AgonyResponse;
import toy.bookchat.bookchat.domain.agony.api.v1.response.SliceOfAgoniesResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
class AgonyServiceTest {

    @Mock
    private BookShelfRepository bookShelfRepository;
    @Mock
    private AgonyRepository agonyRepository;
    @Mock
    private AgonyRecordRepository agonyRecordRepository;
    @InjectMocks
    private AgonyService agonyService;

    private static AgonyEntity getAgony(Long id, String title, String color) {
        return AgonyEntity.builder()
            .id(id)
            .title(title)
            .hexColorCode(color)
            .bookShelfEntity(mock(BookShelfEntity.class))
            .build();
    }

    @Test
    void 고민_생성_성공() throws Exception {
        BookShelfEntity bookShelfEntity = mock(BookShelfEntity.class);
        CreateBookAgonyRequest createBookAgonyRequest = CreateBookAgonyRequest.builder()
            .title("title")
            .hexColorCode("color")
            .build();

        when(bookShelfRepository.findByIdAndUserId(any(), any())).thenReturn(
            Optional.of(bookShelfEntity));

        agonyService.storeBookShelfAgony(createBookAgonyRequest, 1L, 1L);

        verify(agonyRepository).save(any());
    }

    @Test
    void 서재등록_없이_고민_생성시_예외발생() throws Exception {
        CreateBookAgonyRequest createBookAgonyRequest = mock(CreateBookAgonyRequest.class);

        assertThatThrownBy(() -> {
            agonyService.storeBookShelfAgony(createBookAgonyRequest, 1L, 1L);
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 서재에_등록된_고민_단_건_조회_성공() throws Exception {
        AgonyEntity agonyEntity = getAgony(1L, "고민", "파랑");
        given(agonyRepository.findUserBookShelfAgony(1L, 1L, 1L)).willReturn(Optional.of(agonyEntity));
        AgonyResponse actual = agonyService.searchAgony(1L, 1L, 1L);
        AgonyResponse expect = AgonyResponse.from(agonyEntity);
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void 사용자_서재에_등록된_고민_조회_성공() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());

        AgonyEntity agonyEntity1 = getAgony(1L, "agony1", "blue");
        AgonyEntity agonyEntity2 = getAgony(2L, "agony2", "red");

        List<AgonyEntity> contents = List.of(agonyEntity1, agonyEntity2);
        Slice<AgonyEntity> page = new SliceImpl<>(contents, pageRequest, true);
        when(agonyRepository.findUserBookShelfSliceOfAgonies(1L, 1L, pageRequest, 1L)).thenReturn(
            page);
        SliceOfAgoniesResponse pageOfAgoniesResponse = agonyService.searchSliceOfAgonies(1L, 1L,
            pageRequest, 1L);

        String title = pageOfAgoniesResponse.getAgonyResponseList().get(0).getTitle();
        assertThat(title).isEqualTo("agony1");
    }

    @Test
    void 고민폴더_삭제_성공() throws Exception {
        agonyService.deleteAgony(1L, List.of(1L, 2L, 3L), 1L);

        verify(agonyRecordRepository).deleteByAgoniesIds(any(), any(), any());
        verify(agonyRepository).deleteByAgoniesIds(any(), any(), any());
    }

    @Test
    void 고민폴더_수정_성공() throws Exception {
        AgonyEntity agonyEntity = getAgony(1L, "폴더", "파랑");

        ReviseAgonyRequest reviseAgonyRequest = ReviseAgonyRequest.builder()
            .title("폴더 이름 바꾸기")
            .hexColorCode("보라색")
            .build();
        when(agonyRepository.findUserBookShelfAgony(any(), any(), any())).thenReturn(
            Optional.of(agonyEntity));

        agonyService.reviseAgony(1L, 1L, 1L, reviseAgonyRequest);

        String result = agonyEntity.getHexColorCode();
        assertThat(result).isEqualTo("보라색");
    }
}