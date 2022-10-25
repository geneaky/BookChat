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
import toy.bookchat.bookchat.domain.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateBookAgonyRequestDto;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.user.User;

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
        User user = mock(User.class);
        CreateBookAgonyRequestDto createBookAgonyRequestDto = mock(CreateBookAgonyRequestDto.class);

        when(user.getId()).thenReturn(1L);
        when(bookShelfRepository.findByUserIdAndBookId(any(), any())).thenReturn(
            Optional.of(bookShelf));

        agonyService.storeBookAgony(createBookAgonyRequestDto, user, 1L);

        verify(agonyRepository).save(any());
    }

    @Test
    void 서재등록_없이_고민_생성시_예외발생() throws Exception {
        User user = mock(User.class);
        CreateBookAgonyRequestDto createBookAgonyRequestDto = mock(CreateBookAgonyRequestDto.class);

        when(user.getId()).thenReturn(1L);

        assertThatThrownBy(() -> {
            agonyService.storeBookAgony(createBookAgonyRequestDto, user, 1L);
        }).isInstanceOf(BookNotFoundException.class);
    }
}