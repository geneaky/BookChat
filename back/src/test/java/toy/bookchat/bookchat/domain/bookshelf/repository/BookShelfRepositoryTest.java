package toy.bookchat.bookchat.domain.bookshelf.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
public class BookShelfRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;

    @Test
    public void 책_저장() throws Exception {
        Book book = Book.builder()
                .isbn("1234")
                .title("effective java")
                .authors(List.of("Joshua"))
                .publisher("insight")
                .bookCoverImageUrl("bookCover@naver.com")
                .build();

        Book savedBook = bookRepository.save(book);
        assertThat(book).isEqualTo(savedBook);
    }

    @Test
    public void register_book_user_info_at_bookshelf_repository() throws Exception {
        BookShelf bookShelf = BookShelf.builder().build();

        Book book = Book.builder().build();
        book.setBookShelf(bookShelf);
        Book savedBook = bookRepository.save(book);

        User user = User.builder().build();
        user.setBookShelf(bookShelf);
        User savedUser = userRepository.save(user);

        BookShelf savedBookShelf = bookShelfRepository.save(bookShelf);

        assertThat(book).isEqualTo(savedBook);
        assertThat(user).isEqualTo(savedUser);
        assertThat(bookShelf).isEqualTo(savedBookShelf);
    }
}
