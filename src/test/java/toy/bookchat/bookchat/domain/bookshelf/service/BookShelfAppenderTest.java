package toy.bookchat.bookchat.domain.bookshelf.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.user.User;

@ExtendWith(MockitoExtension.class)
class BookShelfAppenderTest {

    @Mock
    private BookShelfRepository bookShelfRepository;
    @InjectMocks
    private BookShelfAppender bookShelfAppender;

    @Test
    void 사용자_서재에_책_추가_성공() throws Exception {
        BookShelf bookShelf = BookShelf.builder().build();
        User user = User.builder().build();
        Book book = Book.builder().build();

        bookShelfAppender.append(bookShelf, user, book);

        verify(bookShelfRepository).save(any());
    }
}