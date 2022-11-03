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
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.request.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.agony.service.dto.response.PageOfAgoniesResponse;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;

@ExtendWith(MockitoExtension.class)
class AgonyServiceTest {

    @Mock
    private BookShelfRepository bookShelfRepository;
    @Mock
    private AgonyRepository agonyRepository;
    @InjectMocks
    private AgonyService agonyService;

    @Test
    void 고민_생성_성공() throws Exception {

        BookShelf bookShelf = mock(BookShelf.class);
        CreateBookAgonyRequestDto createBookAgonyRequestDto = mock(CreateBookAgonyRequestDto.class);

        when(bookShelfRepository.findByUserIdAndBookId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        agonyService.storeBookAgony(createBookAgonyRequestDto, 1L, 1L);

        verify(agonyRepository).save(any());
    }

    @Test
    void 서재등록_없이_고민_생성시_예외발생() throws Exception {
        CreateBookAgonyRequestDto createBookAgonyRequestDto = mock(CreateBookAgonyRequestDto.class);

        assertThatThrownBy(() -> {
            agonyService.storeBookAgony(createBookAgonyRequestDto, 1L, 1L);
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
        PageOfAgoniesResponse pageOfAgoniesResponse = agonyService.searchPageOfAgonies(1L, 1L,
            pageRequest);

        String title = pageOfAgoniesResponse.getAgonyResponseList().get(0).getTitle();
        assertThat(title).isEqualTo("agony1");
    }
}