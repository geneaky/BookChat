package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.COMPLETE;
import static toy.bookchat.bookchat.domain.bookshelf.ReadingStatus.READING;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.agony.repository.AgonyRepository;
import toy.bookchat.bookchat.db_module.agonyrecord.repository.AgonyRecordRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@ExtendWith(MockitoExtension.class)
class BookShelfManagerTest {

    @Mock
    private BookShelfRepository bookShelfRepository;
    @Mock
    private AgonyRepository agonyRepository;
    @Mock
    private AgonyRecordRepository agonyRecordRepository;
    @InjectMocks
    private BookShelfManager bookShelfManager;

    @Test
    void 서재를_저장한다() throws Exception {
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .build();

        bookShelfManager.store(bookShelfEntity);

        verify(bookShelfRepository).save(bookShelfEntity);
    }

    @Test
    void 서재와_관련_기록들을_비운다() throws Exception {
        bookShelfManager.vacate(1L, 2L);

        verify(bookShelfRepository).deleteBookShelfByIdAndUserId(any(), any());
        verify(agonyRecordRepository).deleteByBookShelfIdAndUserId(any(), any());
        verify(agonyRepository).deleteByBookShelfIdAndUserId(any(), any());
    }

    @Test
    void 사용자의_서재를_전부_삭제한다() throws Exception {
        bookShelfManager.remove(1L);

        verify(bookShelfRepository).deleteAllByUserId(any());
    }

    @Test
    void 서재의_독서_상태를_변경_성공() throws Exception {
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder().readingStatus(READING).build();
        given(bookShelfRepository.findById(any())).willReturn(Optional.of(bookShelfEntity));
        bookShelfManager.modify(BookShelf.builder().id(1L).build(), COMPLETE);

        assertThat(bookShelfEntity.getReadingStatus()).isEqualTo(COMPLETE);
    }
}