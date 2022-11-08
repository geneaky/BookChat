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
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.DeleteAgoniesRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.request.ReviseAgonyRequest;
import toy.bookchat.bookchat.domain.agony.service.dto.response.BasePageOfAgoniesResponse;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;

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

    @Test
    void 고민_생성_성공() throws Exception {

        BookShelf bookShelf = mock(BookShelf.class);
        CreateBookAgonyRequest createBookAgonyRequest = mock(CreateBookAgonyRequest.class);

        when(bookShelfRepository.findByUserIdAndBookId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        agonyService.storeBookAgony(createBookAgonyRequest, 1L, 1L);

        verify(agonyRepository).save(any());
    }

    @Test
    void 서재등록_없이_고민_생성시_예외발생() throws Exception {
        CreateBookAgonyRequest createBookAgonyRequest = mock(CreateBookAgonyRequest.class);

        assertThatThrownBy(() -> {
            agonyService.storeBookAgony(createBookAgonyRequest, 1L, 1L);
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 사용자_서재에_등록된_고민_조회_성공() throws Exception {
        PageRequest pageRequest = PageRequest.of(1, 1, Sort.by("id").descending());

        Agony agony1 = Agony.builder()
            .title("agony1")
            .hexColorCode("red")
            .bookShelf(mock(BookShelf.class))
            .build();
        Agony agony2 = Agony.builder()
            .title("agony2")
            .hexColorCode("blue")
            .bookShelf(mock(BookShelf.class))
            .build();

        List<Agony> contents = List.of(agony1, agony2);
        Page<Agony> page = new PageImpl<>(contents, pageRequest, 2);
        when(agonyRepository.findUserBookShelfPageOfAgonies(1L, 1L, pageRequest)).thenReturn(page);
        BasePageOfAgoniesResponse pageOfAgoniesResponse = agonyService.searchPageOfAgonies(1L, 1L,
            pageRequest);

        String title = pageOfAgoniesResponse.getAgonyResponseList().get(0).getTitle();
        assertThat(title).isEqualTo("agony1");
    }

    @Test
    void 고민폴더_삭제_성공() throws Exception {
        DeleteAgoniesRequest deleteAgoniesRequest = DeleteAgoniesRequest.of(List.of(1L, 2L, 3L));
        agonyService.deleteAgony(1L, deleteAgoniesRequest, 1L);

        verify(agonyRecordRepository).deleteByAgoniesIds(any(), any(), any());
        verify(agonyRepository).deleteByAgoniesIds(any(), any(), any());
    }

    @Test
    void 고민폴더_수정_성공() throws Exception {
        Agony agony = Agony.builder()
            .title("폴더")
            .hexColorCode("파랑")
            .build();
        ReviseAgonyRequest reviseAgonyRequest = ReviseAgonyRequest.builder()
            .agonyTitle("폴더 이름 바꾸기")
            .agonyColor("보라색")
            .build();
        when(agonyRepository.findUserBookShelfAgony(any(), any(), any())).thenReturn(
            Optional.of(agony));

        agonyService.reviseAgony(1L, 1L, 1L, reviseAgonyRequest);

        String result = agony.getHexColorCode();
        assertThat(result).isEqualTo("보라색");
    }
}