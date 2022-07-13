package toy.bookchat.bookchat.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

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
        Book book = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");

        Book savedBook = bookRepository.save(book);
        assertThat(book).isEqualTo(savedBook);
    }

    @Test
    public void 책장에_책을_저장() throws Exception {

        BookShelf bookShelf = BookShelf.builder().build();

        Book book = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");

        book.setBookShelf(bookShelf);
        Book savedBook = bookRepository.save(book);

        User user = User.builder()
            .build();
        user.setBookShelf(bookShelf);
        User savedUser = userRepository.save(user);

        BookShelf savedBookShelf = bookShelfRepository.save(bookShelf);

        System.out.println(savedBookShelf.getBook());

        assertThat(book).isEqualTo(savedBook);
        assertThat(user).isEqualTo(savedUser);
        assertThat(bookShelf).isEqualTo(savedBookShelf);
    }

    @Test
    public void 읽고있는_책을_조회() throws Exception {
        Book book = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        bookRepository.save(book);

        User user = User.builder().build();
        userRepository.save(user);

        BookShelf bookShelf = BookShelf.builder()
            .book(book)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        book.setBookShelf(bookShelf);
        user.setBookShelf(bookShelf);

        bookShelfRepository.save(bookShelf);

        bookShelfRepository.findSpecificReadingStateBookByUserId(ReadingStatus.READING, ,user)
    }
}
