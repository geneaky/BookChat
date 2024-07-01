package toy.bookchat.bookchat.domain.agony.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.domain.agony.Agony;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@ExtendWith(MockitoExtension.class)
class AgonyAppenderTest {

    @Mock
    private AgonyRepository agonyRepository;
    @InjectMocks
    private AgonyAppender agonyAppender;

    @Test
    void 책장에_고민_추가_성공() throws Exception {
        Agony agony = mock(Agony.class);
        BookShelf bookShelf = mock(BookShelf.class);

        agonyAppender.append(agony, bookShelf);

        verify(agonyRepository).save(any());
    }
}