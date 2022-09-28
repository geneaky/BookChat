package toy.bookchat.bookchat.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import toy.bookchat.bookchat.config.JpaAuditingConfig;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.book.repository.BookRepository;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;
import toy.bookchat.bookchat.domain.configuration.TestConfig;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@DataJpaTest
@Import({JpaAuditingConfig.class,TestConfig.class})
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
        Book book1 = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        Book book2 = new Book("12345", "effective java2", List.of("Joshua"), "insight",
            "bookCove2r@naver.com");
        bookRepository.save(book1);
        bookRepository.save(book2);

        User user = User.builder().build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        BookShelf bookShelf2 = BookShelf.builder()
            .book(book2)
            .user(user)
            .readingStatus(ReadingStatus.READING)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        book1.setBookShelf(bookShelf1);
        book2.setBookShelf(bookShelf2);
        user.setBookShelf(bookShelf1);
        user.setBookShelf(bookShelf2);

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Direction.DESC, "id"));
        List<BookShelf> bookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.READING, pageable, user.getId());

        assertThat(bookShelves.size()).isEqualTo(2);
    }

    @Test
    public void 읽은_책을_조회() throws Exception {
        Book book1 = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        Book book2 = new Book("12345", "effective java2", List.of("Joshua"), "insight",
            "bookCove2r@naver.com");
        bookRepository.save(book1);
        bookRepository.save(book2);

        User user = User.builder().build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.COMPLETE)
            .star(Star.THREE_HALF)
            .singleLineAssessment("재밌네요 허허")
            .build();

        BookShelf bookShelf2 = BookShelf.builder()
            .book(book2)
            .user(user)
            .readingStatus(ReadingStatus.COMPLETE)
            .star(Star.FIVE)
            .singleLineAssessment("이시대 최고의 도서")
            .build();

        book1.setBookShelf(bookShelf1);
        book2.setBookShelf(bookShelf2);
        user.setBookShelf(bookShelf1);
        user.setBookShelf(bookShelf2);

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Direction.DESC, "id"));
        List<BookShelf> bookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.COMPLETE, pageable, user.getId());

        assertThat(bookShelves.size()).isEqualTo(2);
    }

    @Test
    public void 읽을_책을_조회() throws Exception {
        Book book1 = new Book("1234", "effective java", List.of("Joshua"), "insight",
            "bookCover@naver.com");
        Book book2 = new Book("12345", "effective java2", List.of("Joshua"), "insight",
            "bookCove2r@naver.com");
        bookRepository.save(book1);
        bookRepository.save(book2);

        User user = User.builder().build();
        userRepository.save(user);

        BookShelf bookShelf1 = BookShelf.builder()
            .book(book1)
            .user(user)
            .readingStatus(ReadingStatus.WISH)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        BookShelf bookShelf2 = BookShelf.builder()
            .book(book2)
            .user(user)
            .readingStatus(ReadingStatus.WISH)
            .star(Star.ZERO)
            .singleLineAssessment(null)
            .build();

        book1.setBookShelf(bookShelf1);
        book2.setBookShelf(bookShelf2);
        user.setBookShelf(bookShelf1);
        user.setBookShelf(bookShelf2);

        bookShelfRepository.save(bookShelf1);
        bookShelfRepository.save(bookShelf2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Direction.DESC, "id"));
        List<BookShelf> bookShelves = bookShelfRepository.findSpecificStatusBookByUserId(
            ReadingStatus.WISH, pageable, user.getId());

        assertThat(bookShelves.size()).isEqualTo(2);
    }
}
