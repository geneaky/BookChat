package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.exception.notfound.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookShelfReaderTest {

    @Mock
    private BookShelfRepository bookShelfRepository;
    @InjectMocks
    private BookShelfReader bookShelfReader;

    @Test
    void 사용자id_서재id와_일치하는_서재가없다면_예외_발생() throws Exception {
        assertThatThrownBy(() -> bookShelfReader.readBookShelfEntity(488L, 897L))
            .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 사용자id_읽음상태조건과_일치하는_서재를_조회한다() throws Exception {
        bookShelfReader.readBookShelfEntity(705L, ReadingStatus.COMPLETE, mock(Pageable.class));

        verify(bookShelfRepository).findSpecificStatusBookByUserId(any(), any(), any());
    }

    @Test
    void 사용자id_isbn_발행일자와_일치하는_서재를_조회한다() throws Exception {
        assertThatThrownBy(() -> {
            bookShelfReader.readBookShelfEntity(705L, "isbn", LocalDate.now());
        }).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void 사용자_서재_조회_성공() throws Exception {
        BookShelfEntity bookShelfEntity = BookShelfEntity.builder().id(1L).build();
        given(bookShelfRepository.findByIdAndUserId(any(), any())).willReturn(Optional.of(bookShelfEntity));
        BookShelf bookShelf = bookShelfReader.readBookShelf(1L, 1L);

        assertThat(bookShelf.getId()).isEqualTo(1L);
    }

    @Test
    void 사용자_미보유_서재_조회_실패() throws Exception {
        assertThatThrownBy(() -> bookShelfReader.readBookShelf(1L, 1L)).isInstanceOf(BookNotFoundException.class);
    }
}